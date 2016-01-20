package exam.sample.hotel.service.database

import exam.sample.hotel.Config
import exam.sample.hotel.service.model.Hotel
import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationWithJUnit

class HotelRepoTest extends SpecificationWithJUnit with  Mockito {


  "Hotel repository " should {

    "initlize all object " in {

      val datasource = new CSVDataSource(Config.databaseFile)

      datasource.size === 26
    }

    "properly serach hotels " in {

      val datasource = new CSVDataSource(Config.databaseFile)
      val bangkokCities: Seq[Hotel] = datasource.findHotelsByCityId("Bangkok")
      bangkokCities.size === 7
      bangkokCities.map(_.cityId).distinct === List("Bangkok")

    }
  }
}
