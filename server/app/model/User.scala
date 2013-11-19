package model

import java.sql.Date
import scala.slick.driver.PostgresDriver.simple._


/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 28.10.13
 * Time: 19:31
 * To change this template use File | Settings | File Templates.
 */
case class User(id: String,
                cities: Int,
                countries: Int,
                registerDate: Option[Date],
                ageFrom: Option[Int],
                ageTo: Option[Int],
                male: Option[Boolean],
                address: String,
                reviews: Option[Int],
                thanks: Option[Int],
                countryIso: Option[String]) {

}

object Users extends Table[User]("users_table") {

  def id = column[String]("id", O.PrimaryKey)

  def cities = column[Int]("cities")

  def countries = column[Int]("countries")

  def registerDate = column[Option[Date]]("register_date")

  def ageFrom = column[Option[Int]]("ageFrom")

  def ageTo = column[Option[Int]]("ageTo")

  def male = column[Option[Boolean]]("male")

  def address = column[String]("address")

  def reviews = column[Option[Int]]("reviews")

  def thanks = column[Option[Int]]("thanks")

  def countryIso = column[Option[String]]("country_iso")

  def * = id ~ cities ~ countries ~ registerDate ~ ageFrom ~ ageTo ~
    male ~ address ~ reviews ~ thanks ~ countryIso <>(User.apply _, User.unapply _)
}