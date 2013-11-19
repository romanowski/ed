import java.sql.Date
import java.text.DateFormat
import model.{Review, Reviews, Hotel, Hotels}
import scala.slick.driver.PostgresDriver.simple._
import util.Try


object Importer {


  def importReviews(data: Seq[Map[String, String]], hotelData: Map[String, String]) = {

    DatabaseConf.db.withSession {
      implicit session: Session =>
        try {
          val hotelId = hotelData("id")
          Hotels.insert(Hotel(
            id = hotelData("id"),
            name = "",
            overall = hotelData("Overall Rating").toDouble,
            avgPrice = Try {
              hotelData("Avg. Price").tail.toDouble
            }.getOrElse(0.0),
            url = hotelData.get("URL").getOrElse(""),
            address = "",
            countryIso = None
          ))

          data.foreach {
            data =>
              try {
                Reviews.insert(
                  Review(
                    hotel = hotelId,
                    user = data("Author"),
                    date = new Date(DateFormat.getDateInstance(DateFormat.MEDIUM, java.util.Locale.US)
                      .parse(data("Date")).getTime),
                    review = data("Content"),
                    reader = data.get("No. Reader").map(_.toInt),
                    helpful = data.get("No. Helpful").map(_.toInt),
                    overall = data.get("Overall").map(_.toInt),
                    valueRate = data.get("Value").map(_.toInt),
                    rooms = data.get("Rooms").map(_.toInt),
                    location = data.get("Location").map(_.toInt),
                    clean = data.get("Cleanliness").map(_.toInt),
                    checkIn = data.get("Check in / front desk").map(_.toInt),
                    service = data.get("Service").map(_.toInt),
                    businessService = data.get("Business service").map(_.toInt)
                  )
                )
              } catch {
                case e =>
                  println(s"during import review: $data")
                  e.printStackTrace()
              }
          }
        } catch {
          case e: Throwable =>
            println(s"during import hotel of $hotelData")
            e.printStackTrace()
        }
    }
  }
}
