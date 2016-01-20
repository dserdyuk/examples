package exam.sample.hotel.service

import java.util.Date

import exam.sample.hotel._
import org.slf4j.LoggerFactory

import scala.collection._
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}


trait RateLimitGuard {

  def checkLimit(key: String): Try[Unit]

  def size(): Int
}

object RateLimitGuard {
  def apply() = {
    new InMemoryRateLimitGuard(Config.rateLimitDuration, Config.suspensionPeriod, Config.idleKeyTimeout, Config.maxApiCacheSize)
  }
}

/**
 *
 * @param defaultRateLimit  default duration between
 * @param suspendFor duration API key need to suspended if limit reached
 * @param idleTimeout duration form last time used when service remove
 * @param maxCacheSize limit of cached API keys
 */
class InMemoryRateLimitGuard(defaultRateLimit: Duration, suspendFor: Duration, idleTimeout: Duration,
                             maxCacheSize: Int = Integer.MAX_VALUE) extends RateLimitGuard {

  def logger = LoggerFactory.getLogger(this.getClass)

  private val apiKeys = new concurrent.TrieMap[String, ApiKey]()

  private def addNewKey(newApiKey: ApiKey, nowTime: Long) = {
    if (logger.isDebugEnabled) {
      logger.debug(s"Key=${newApiKey.key} added, lastused=${new Date(newApiKey.lastUsed)}, now=${new Date(nowTime)}")
    }
    apiKeys.put(newApiKey.key, newApiKey)

    if (apiKeys.size > maxCacheSize) {
      //XXX: depending on SLA we can either do cleanup synchronously, schedule async job or or do partial clean by iteration over n % of the map
      // for simplicity let's do it synchronously .
      val idleInMillis = idleTimeout.toMillis
      val sizeBeforeCleanUp = apiKeys.size
      apiKeys.retain { case (_, apiKey) =>
        (nowTime - apiKey.lastUsed) < idleInMillis
      }
      if (logger.isInfoEnabled) {
        logger.info(s"${sizeBeforeCleanUp - apiKeys.size} keys has been retained ")
      }

    }
  }

  def checkLimit(key: String): Try[Unit] = {
    apiKeys.get(key) match {
      case Some(apiKey) =>
        val result = apiKey.synchronized {
          val now = System.currentTimeMillis()
          apiKey match {
            case ApiKey(_, _, limit, Some(suspendedTill)) if isSuspended(now, suspendedTill) =>
              Failure(new KeySuspendedException("still suspended"))
            case ApiKey(_, lastUsed, limit, None) if shouldBeSuspended(now, lastUsed, limit) =>
              suspendApiKey(apiKey, now)
              Failure(new KeySuspendedException("suspended"))
            case _ =>
              renewApiKey(apiKey, now)
              Success()
          }
        }
        result
      case None =>
        val now = System.currentTimeMillis()
        val newApiKey = ApiKey(key, lastUsed = now, rateLimit =
          Config.predefinedKeys.getOrElse(key, defaultRateLimit))
        addNewKey(newApiKey, now)
        Success()
    }
  }

  protected def isSuspended(now: Long, suspendedTill: Long): Boolean = {
    suspendedTill >= now
  }

  protected def shouldBeSuspended(now: Long, lastUsed: Long, limit: Duration): Boolean = {
    (now - lastUsed) <= limit.toMillis
  }

  protected def renewApiKey(apiKey: ApiKey, now: Long) = {
    if (logger.isDebugEnabled) {
      logger.debug(s"Key=${apiKey.key} renewed, lastused=${new Date(apiKey.lastUsed)}, now = ${new Date(now)}, suspended= ${apiKey.suspendedTill.map(new Date(_))}")
    }

    apiKey.lastUsed = now
    apiKey.suspendedTill = None

  }

  protected def suspendApiKey(apiKey: ApiKey, now: Long) = {
    if (logger.isDebugEnabled) {
      logger.debug(s"Key=${apiKey.key} suspended, lastused=${new Date(apiKey.lastUsed)}, now=${new Date(now)}")
    }

    apiKey.lastUsed = now
    apiKey.suspendedTill = Option(now + suspendFor.toMillis)

  }

  override def size(): Int = apiKeys.size
}

case class ApiKey(key: String, protected[service] var lastUsed: Long, rateLimit: Duration, protected[service] var suspendedTill: Option[Long] = None)


class KeySuspendedException(reason: String) extends RuntimeException 
