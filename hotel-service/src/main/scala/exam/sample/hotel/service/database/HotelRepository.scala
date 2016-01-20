package exam.sample.hotel.service.database

import java.io.{File, FileReader, IOException}
import java.net.URI

import com.github.tototoshi.csv.{CSVReader, DefaultCSVFormat}
import exam.sample.hotel.service.model.{Hotel, HotelRootType}
import org.slf4j.LoggerFactory

import scala.util.{Sorting, Try}


trait HotelRepository {

  def findHotelsByCityId(cityId: String, sortAscByPrice: Option[Boolean] = None): Seq[Hotel]

  def size(): Long

}


class CSVDataSource(fileLocation: URI) extends HotelRepository {

  def logger = LoggerFactory.getLogger(this.getClass)

  lazy val data = init(fileLocation)

  override val size: Long = data.size

  def init(fileLocation: URI) = {

    val format = new DefaultCSVFormat {
      override val separator: Char = ','
      override val quoteChar = '"'
      override val numberOfLinesToSkip = 1
    }

    try {
      val reader = CSVReader.open(new FileReader(new File(fileLocation)))(format)
      val lines = reader.all()
      lines.filter(_.size >= 4).map { case columns =>
        Try {
          Hotel(columns(0), columns(1).toLong, HotelRootType.withName(columns(2)), BigDecimal(columns(3)))
        }.getOrElse(null)
      }.toArray
    } catch {
      case e: IOException =>
        logger.error("Failed to load database ", e)
        Array[Hotel]()
      case re: RuntimeException =>
        logger.error("Failed to parse database ", re)
        Array[Hotel]()
    }
  }

  override def findHotelsByCityId(cityId: String, sortAscByPrice: Option[Boolean]): Seq[Hotel] = {
    val hotels = data.filter(_.cityId == cityId)
    sortAscByPrice.foreach { case asc =>
      Sorting.quickSort(hotels)(Ordering.by(_.price))
    }
    hotels
  }
}