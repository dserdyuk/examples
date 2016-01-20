package exam.sample

import java.text.SimpleDateFormat

import akka.actor.ActorSystem
import exam.sample.hotel.service.http.reqres.HotelsResponse
import exam.sample.hotel.service.model.HotelRootType.HotelRootType
import exam.sample.hotel.service.model.{Hotel, HotelRootType}
import spray.json._

package object hotel {
  implicit val system = ActorSystem("Hotel-service")

  implicit val ec = system.dispatcher

  val defaultDateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

  implicit object HotelRootTypeJsonFormat extends RootJsonFormat[HotelRootType] {
    def write(obj: HotelRootType): JsValue = JsString(obj.toString)

    def read(json: JsValue): HotelRootType = json match {
      case JsString(str) => HotelRootType.withName(str)
      case _ => throw new DeserializationException("Enum string expected")
    }
  }

  object MyJsonProtocol extends DefaultJsonProtocol {

    implicit val hotelFormat = jsonFormat4(Hotel)
    implicit val hotelResponseFormat = jsonFormat(HotelsResponse, "hotels")

  }


}
