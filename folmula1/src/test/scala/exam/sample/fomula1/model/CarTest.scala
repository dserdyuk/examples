package exam.sample.fomula1.model

import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope


class CarTest extends SpecificationWithJUnit with Mockito {


  "Car  " should {

    "accelerate to to max speed " in new FakeTrack {
      val carToTest = Car(1, 0)
      carToTest.topSpeed.round === 44.0
      carToTest.acceleration === 2

      carToTest.speed === 0.0
      //      val driver = Driver("test", carToTest,new Track(1000,1))
      carToTest.updateOnTick(track.updatePeriodInSec)
      val speed2 = carToTest.speed
      val d2 = carToTest.distance
      speed2 must be greaterThan 0.0
      d2 must be greaterThan 0.0
      carToTest.updateOnTick(track.updatePeriodInSec)
      carToTest.speed must be greaterThan speed2
      carToTest.distance must be greaterThan d2
      carToTest.speed === 8.0

      carToTest.updateOnTick(track.updatePeriodInSec)
      carToTest.speed === 12.0

      (0 to 20).foreach { _ => carToTest.updateOnTick(track.updatePeriodInSec)}

      carToTest.speed === carToTest.topSpeed

      carToTest.useNitro()

      carToTest.speed === carToTest.topSpeed

    }
  }


  trait FakeTrack extends Scope {

    val track = new TrackController() {
      override val trackLength: Double = 1000.0
      override val drivers: List[Driver] = Nil
      override val cars: List[Car] = Nil
      override val teamsNumber: Int = 1

      override def updatePeriodInSec = 2
    }

  }

}
