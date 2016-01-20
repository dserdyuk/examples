package exam.sample.hotel

import akka.actor.Props
import akka.io.IO
import exam.sample.hotel.service.http.HotelServiceRouter
import org.slf4j.LoggerFactory
import spray.can.Http

/**
 * Hello world!
 *
 */
object HotelServiceApp extends App {


  def logger = LoggerFactory.getLogger(this.getClass)

  logger.info("Starting hotel service...")

  val service = system.actorOf(Props[HotelServiceRouter], "hotel-service")

  IO(Http) ! Http.Bind(service, interface = "0.0.0.0", port = Config.port)

  logger.info(s"Service started on port: ${Config.port}")

}

