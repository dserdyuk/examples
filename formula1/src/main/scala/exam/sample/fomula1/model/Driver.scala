package exam.sample.fomula1.model

import org.slf4j.LoggerFactory


trait IDriver {

  def lookAround(): Unit

  def slowDown()

  def forsage()

}

object Driver {

  def apply(name: String, car: Car, track: TrackController) = new Driver(name, car, track)
}

class Driver(name: String, car: Car, track: TrackController) extends IDriver {

  val logger = LoggerFactory.getLogger(Driver.getClass)

  def slowDown() = {
    car.applyHf()
    logger.debug(s"Car[${car.startPosition}] is slowing  down")
  }

  def forsage() = {
    car.useNitro()
  }

  def lookAround(): Unit = {
    //first make sure there is no danger  of impact
    if (track.getClosestCar(car).isDefined) {
      slowDown()
    } else if (track.getLastCar() == car && track.cars.size > 1) {
      forsage()
    } else {
      logger.trace(s"Car[${car.startPosition}] keeps going")
    }
  }
}