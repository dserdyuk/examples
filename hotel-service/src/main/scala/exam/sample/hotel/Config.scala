package exam.sample.hotel

import org.slf4j.LoggerFactory

import scala.concurrent.duration.{Duration, _}

//TODO: add HConf support
object Config {
  def logger = LoggerFactory.getLogger(this.getClass)

  val maxApiCacheSize: Int = 100

  val idleKeyTimeout = 30.minutes

  val suspensionPeriod = 5.minutes
  val rateLimitDuration = 10.seconds

  val port = 8090

  val predefinedKeys = Map[String, Duration]("test-1" -> 1.minutes, "test-2" -> 2.minutes)

  val databaseFile = {
    try {
      getClass.getResource("/hoteldb.csv").toURI
    } catch {
      case e: Exception =>
        logger.error("failed to find DB file", e)
        throw e
    }
  }
}
