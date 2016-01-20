package exam.sample.hotel.service.model

import exam.sample.hotel.service.model.HotelRootType.HotelRootType


case class Hotel(cityId: String, hotelId: Long, roomType: HotelRootType, price: BigDecimal)

object HotelRootType extends Enumeration {
  type HotelRootType = HotelRootType.Value

  val Deluxe = Value("Deluxe")
  val Superior = Value("Superior")
  val SweetSuite = Value("Sweet Suite")
}