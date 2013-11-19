import annotation.tailrec
import io.Source
import java.net.{URLDecoder, URLEncoder}
import model._
import model.Country
import scala.slick.driver.PostgresDriver.simple._
import scala.Some
import util.parsing.json.JSON

object CountriesCrawler {


  val countryForIso = for {
    iso <- Parameters[String]
    country <- Countries if country.iso === iso
  } yield country

  def hotelForId(id: String) = for {
    hotel <- Hotels if hotel.id === id
  } yield hotel

  def hotelToDo = for {
    hotel <- Hotels if hotel.countryIso.isNull && hotel.name.isNotNull
  } yield hotel


  def userForId(id: String) = for {
    user <- Users if user.id === id
  } yield user

  def userToDo = for {
    user <- Users
  } yield user

  def regionToDo = for {
    region <- Regions if region.lat.isNull
  } yield region


  def regionUpdate(name: String) = for {
    region <- Regions if region.name === name
  } yield region

  val region = for {
    n <- Parameters[String]
    region <- Regions if region.name === n
  } yield region.iso


  object GoogleHelper {

    def data(name: String) = {
      val url = s"http://maps.googleapis.com/maps/api/geocode/json?address=${URLEncoder.encode(name, "utf-8")}&sensor=false"
      val textData = Source.fromURL(url).mkString
      JSON.parseFull(textData)
        .flatMap(field("results"))
        .flatMap(list)
        .flatMap(_.headOption)
    }

    def field(name: String)(d: Any): Option[Any] = d match {
      case m: Map[String, Any] => m.get(name)
      case _ => None
    }

    def list(d: Any): Option[List[Any]] = d match {
      case l: List[Any] => Some(l)
      case _ => None
    }

    def latLng(name: String): (Option[Double], Option[Double]) = {
      val location = data(name)
        .flatMap(field("geometry")).flatMap(field("location"))
      (location.flatMap(field("lat")).map(_.toString.toDouble).orElse(Some(-9999)),
        location.flatMap(field("lng")).map(_.toString.toDouble).orElse(Some(-9999))
        )
    }

    def crawlCountry(name: String)(implicit session: Session): Option[String] = {

      def country(d: Any): Option[(String, String)] = d match {
        case m: Map[String, Any] =>
          val a = m.get("types")
          m.get("types").flatMap {
            case l: List[Any] if l.contains("country") =>
              Some(m("long_name").toString -> m("short_name").toString)
            case _ => None
          }
        case _ => None
      }

      val rawData = data(name)

      rawData
        .flatMap(field("address_components"))
        .flatMap(list)
        .flatMap(_.flatMap(country).headOption)
        .orElse(Some("no-country", "##"))
        .map {
        case (countryName, iso) =>
          Regions.insert((iso, name, None, None))
          countryForIso.firstOption(iso).orElse {
            Countries.insert(Country(iso, countryName))
            None
          }
          println(iso)
          iso
      }
    }
  }


  @tailrec
  def crawlSingleHotel: Unit = {
    if (DatabaseConf.db.withSession {
      implicit session =>
        hotelToDo.firstOption().map {
          case hotel =>
            hotelForId(hotel.id).update(
              hotel.copy(
                countryIso = region.firstOption(hotel.address).orElse(GoogleHelper.crawlCountry(hotel.address))
              )
            )
            true
        }.getOrElse(false)
    }) crawlSingleHotel
  }

  @tailrec
  def crawlSingleUser: Unit = {
    if (DatabaseConf.db.withSession {
      implicit session =>
        userToDo.firstOption().map {
          case user =>
            userForId(user.id).update(
              user.copy(
                countryIso = region.firstOption(user.address).orElse(GoogleHelper.crawlCountry(user.address))
              )
            )
            true
        }.getOrElse(false)
    }) crawlSingleUser
  }

  @tailrec
  def crawlRegionLatLng: Unit = if (DatabaseConf.db.withSession {
    implicit session =>
      regionToDo.firstOption().map {
        case (iso, name, _, _) =>
          val (lat, lng) = GoogleHelper.latLng(name)
          println(s"$name - lat: $lat lng: $lng")
          regionUpdate(name).update((iso, name, lat, lng))
          true
      }.getOrElse(false)
  }) crawlRegionLatLng


}

object CountriesCrawlerHotels extends App {
  CountriesCrawler.crawlSingleHotel
}

object CountriesCrawlerUsers extends App {
  CountriesCrawler.crawlSingleUser
}

object RegionsDataCrawler extends App {
  CountriesCrawler.crawlRegionLatLng
}