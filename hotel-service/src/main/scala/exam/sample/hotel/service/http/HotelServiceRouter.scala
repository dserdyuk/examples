package exam.sample.hotel.service.http

import exam.sample.hotel.MyJsonProtocol._
import exam.sample.hotel.service.database.CSVDataSource
import exam.sample.hotel.service.http.reqres.HotelsResponse
import exam.sample.hotel.service.{HotelService, RateLimitGuard}
import exam.sample.hotel.{Config, MyJsonProtocol}
import org.slf4j.LoggerFactory
import spray.http.MediaTypes._
import spray.httpx.SprayJsonSupport._
import spray.routing._

import scala.util.Try

class HotelServiceRouter extends HttpServiceActor {

  def logger = LoggerFactory.getLogger(this.getClass)

  val hotelService = new HotelService(new CSVDataSource(Config.databaseFile))

  val rateLimitGuard = RateLimitGuard()

  def receive = runRoute(route)

  /**
   * Handle post requests and execute appropriate command handler
   */
  val route =


    respondWithMediaType(`application/json`) {

      (path("hotels" / Segment) & get) {
        cityId =>
          parameter("sortAsc" ?) { sort =>
            headerValueByName("Api-Key") { key =>
              val checkTry = rateLimitGuard.checkLimit(key)

              validate(checkTry.isSuccess, s"rate limit has been exceeded for key $key") {

                handleGetHotelsByCity(cityId, sort.map(_.toBoolean))
              }

            }

          }
      }
    }

  def handleGetHotelsByCity(cityId: String, sortAsc: Option[Boolean])(ctx: RequestContext) = {

    ctx.complete {
      val result: Try[HotelsResponse] = Try[HotelsResponse] {
        HotelsResponse(hotelService.findByCity(cityId, sortAsc).toList)
      }
      result
    }
  }

}
