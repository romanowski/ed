package model

import scala.slick.driver.PostgresDriver.simple._

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 21.10.13
 * Time: 18:31
 * To change this template use File | Settings | File Templates.
 */
case class Hotel(id: String, name: String, overall: Double, avgPrice: Double, url: String, address: String, countryIso: Option[String]) {

}


object Hotels extends Table[Hotel]("hotels") {


  def id = column[String]("id")

  def name = column[String]("name")

  def overall = column[Double]("overall")

  def avgPrice = column[Double]("avgPrice")

  def url = column[String]("url")

  def address = column[String]("address")

  def countryIso = column[Option[String]]("country_iso")

  def countryIsoKey = foreignKey("countryIsoKey", countryIso, Countries)(_.iso)


  def * = id ~ name ~ overall ~ avgPrice ~ url ~ address ~ countryIso <>(Hotel.apply _, Hotel.unapply _)
}