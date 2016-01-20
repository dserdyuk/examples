package exam.sample.fomula1.model

/**
 * Created by dserdiuk on 1/18/16.
 */
class Track(val trackLength: Double, val teamsNumber: Int, distanceDelay: Double = 200.0) extends TrackController {

  lazy val cars = init()
  lazy val drivers = cars.map { case car =>
    Driver(car.startPosition + " car ", car, this)
  }

  override def updatePeriodInSec = 2

  def init() = {
    (1 to teamsNumber).map { case startPosition => Car(startPosition, (teamsNumber - startPosition) * distanceDelay)}.toList

  }

  override def toString: String = {
    cars.mkString("\n")
  }
}

trait TrackController {
  val trackLength: Double
  val teamsNumber: Int
  val cars: List[Car]
  val drivers: List[Driver]

  def updatePeriodInSec: Int


  def findWinner(): Option[Car] = {
    val possibleWinners = cars.filter(_.distance >= trackLength).toList
    possibleWinners match {
      case winner :: Nil => Option(winner)
      case _ :: tail =>
        // in cse we have very close competitors let's choose the one who make longest distance on track
        Option(possibleWinners.maxBy(_.distance))
      case Nil => None
    }
  }

  def getClosestCar(car: Car) = {
    cars.filter(_ != car).find { case neighbor => Math.abs(neighbor.distance - car.distance) <= 10.0}
  }

  def getLastCar() = {
    cars.minBy(_.distance)
  }

  def updateLoop() = {
    cars.foreach(_.updateOnTick(updatePeriodInSec))
    drivers.foreach(_.lookAround())

  }

}
