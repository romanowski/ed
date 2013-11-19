package model

import scala.slick.driver.PostgresDriver.simple._
import java.sql.Date


/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 21.10.13
 * Time: 18:32
 * To change this template use File | Settings | File Templates.
 */
case class Review(hotel: String,
                  user: String,
                  date: Date,
                  review: String,
                  reader: Option[Int],
                  helpful: Option[Int],
                  overall: Option[Int],
                  valueRate: Option[Int],
                  rooms: Option[Int],
                  location: Option[Int],
                  clean: Option[Int],
                  checkIn: Option[Int],
                  service: Option[Int],
                  businessService: Option[Int]) {

}

object Reviews extends Table[Review]("reviews") {

  def hotel = column[String]("hotel")

  def user = column[String]("user")

  def date = column[Date]("date")

  def review = column[String]("review")

  def reader = column[Option[Int]]("reader")

  def helpful = column[Option[Int]]("helpful")

  def overall = column[Option[Int]]("overall")

  def valueRate = column[Option[Int]]("valueRate")

  def rooms = column[Option[Int]]("rooms")

  def location = column[Option[Int]]("location")

  def clean = column[Option[Int]]("clean")

  def checkIn = column[Option[Int]]("check_in")

  def service = column[Option[Int]]("service")

  def businessService = column[Option[Int]]("business_service")

  def * = hotel ~ user ~ date ~ review ~ reader ~ helpful ~ overall ~ valueRate ~
    rooms ~ location ~ clean ~ checkIn ~ service ~ businessService <>(Review.apply _, Review.unapply _)

}