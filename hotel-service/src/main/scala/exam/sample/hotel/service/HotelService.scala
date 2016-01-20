package exam.sample.hotel.service

import exam.sample.hotel.service.database.HotelRepository


class HotelService(hotels: HotelRepository) {

  def findByCity(cityId: String, sortAsc: Option[Boolean]) = {
    hotels.findHotelsByCityId(cityId, sortAsc)
  }
}
