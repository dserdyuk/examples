package exam.sample.formula1.model

import org.slf4j.LoggerFactory

/**
 * All attributes in SI base units.
 *
 * @param startPosition
 */
class Car(val startPosition: Int, startDistance: Double) {

  val logger = LoggerFactory.getLogger(Car.getClass)

  private val hf = 0.8

  val acceleration = 2 * startPosition
  // converted to SI
  val topSpeed = (150 + 10 * startPosition) * 5.0 / 18.0

  private var nitroUsage = 0
  /**
   * Current speed in meter per second
   */
  private var _speed = 0.0
  private var _distance = startDistance

  protected[formula1] def updateOnTick(updatePeriodInSec: Int) = {
    _distance = _distance + _speed * updatePeriodInSec + acceleration * (updatePeriodInSec * updatePeriodInSec) / 2.0
    _speed = Math.min(_speed + updatePeriodInSec * acceleration, topSpeed)
  }

  def speed = _speed

  /**
   * Total distance
   */
  def distance = _distance

  def useNitro(): Unit = {
    if (nitroUsage == 0) {
      _speed = Math.min(2.0 * _speed, topSpeed)
      nitroUsage = 1
      logger.info(s" - Car[$startPosition] uses NITRO")
    }
  }

  protected[model] def applyHf(): Unit = {
    _speed = hf * _speed
  }

  override def toString: String = {
    s"car[$startPosition](speed=$speed,distance=$distance, nitro=$nitroUsage)"
  }


  def canEqual(other: Any): Boolean = other.isInstanceOf[Car]

  override def equals(other: Any): Boolean = other match {
    case that: Car =>
      (that canEqual this) &&
        startPosition == that.startPosition
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(startPosition)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object Car {
  def apply(startPosition: Int, startDistance: Double) = {
    if (startPosition <= 0) {
      throw new IllegalArgumentException(" start position should be > 0 ")
    }
    if (startDistance < 0.0) {
      throw new IllegalArgumentException(" start distance  should be > 0 ")
    }
    new Car(startPosition, startDistance: Double)
  }
}






