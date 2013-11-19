import annotation.tailrec
import java.sql.Date
import model.{Users, Reviews, User}
import org.jsoup.Jsoup
import collection.JavaConversions._
import org.jsoup.select.Elements
import util.matching.Regex
import util.Try
import scala.slick.driver.PostgresDriver.simple._


/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 28.10.13
 * Time: 20:09
 * To change this template use File | Settings | File Templates.
 */
object HtmlCrawler {

  val ReviewRegex = """(\d+).+""".r
  val ThanksRegex = """.+ (\d+).+""".r
  val AgeRegex = """(\d+-\d+).+""".r

  val monthMap: Map[String, String] =
    "sty lut mar kwi maj cze lip sie wrz paź lis gru".split(" ").zipWithIndex.toMap.map {
      case (name, nr) if nr < 9 => name -> ("0" + (nr + 1))
      case (name, nr) => name -> (nr + 1).toString
    }

  def crawlUser(name: String): Option[User] = {

    val doc = Jsoup.connect(s"http://pl.tripadvisor.com/members/$name").get()


    //abstract
    def element(selector: String) = doc.select(selector).headOption.map(_.text())

    def inReqEx(regEx: Regex)(data: String): Option[String] = regEx.unapplySeq(data).flatMap(_.headOption)

    def textMixWithDiv(elements: Elements) =
      elements.headOption.toSeq.flatMap(_.textNodes()).map(_.text()).headOption

    def parseDigitNumber(id: String): Int = {
      doc.select(s"#$id .digit").map(_.html()) match {
        case s if s.isEmpty => 0
        case list => list.mkString.toInt
      }
    }

    def registerDate = element(".memberSince").flatMap {
      dateStr => {
        dateStr.split(" ").toSeq match {
          case Seq(_, month, year) =>
            Some(Date.valueOf(s"$year-${monthMap(month)}-01"))
          case _ =>
            None
        }
      }
    }

    def male = element(".memberAge").flatMap(_.split(",").toSeq match {
      case Seq(_, gender) => Some(gender.trim)
      case _ => None
    }).map("kobieta" !=)

    def home = element(".hometown").map(_.split(":")(1).trim).getOrElse("")


    val reviews = textMixWithDiv(doc.select(".totalReviews")).flatMap(inReqEx(ReviewRegex)).map(_.toInt)

    val thanks = textMixWithDiv(doc.select(".helpfulVotes")).flatMap(inReqEx(ThanksRegex)).map(_.toInt)


    val (ageFrom, ageTo) = element(".memberAge")
      .flatMap(inReqEx(AgeRegex)).toSeq.flatMap(_.split("-")).map(_.toInt) match {
      case Seq(ageFrom, ageTo) => Some(ageFrom) -> Some(ageTo)
      case _ => (None, None)
    }

    Try {
      Some(User(
        id = name,
        cities = parseDigitNumber("counter-city-common"),
        countries = parseDigitNumber("counter-country-common"),
        registerDate = registerDate,
        ageFrom = ageFrom,
        ageTo = ageTo,
        male = male,
        address = home,
        reviews = reviews,
        thanks = thanks,
        countryIso = None))
    }.recover {
      case e: Throwable =>
        e.printStackTrace()
        None
    }.get
  }

  def userById(id: String) = for {
    u <- Users if u.id === id
  } yield u

  private val nextUser = for {
    user <- Users if user.countries < -1
  } yield user

  @tailrec
  def crawlAll: Unit = {
    if (

      DatabaseConf.db.withSession {
        implicit session: Session =>
          val next = nextUser.firstOption
          next match {
            case Some(usr) =>
              userById(usr.id).update(usr.copy(countries = -1))
              Try {
                crawlUser(usr.id).foreach(userById(usr.id).update)
              }.recover {
                case e: Throwable => println(e.getMessage)
              }
              true
            case _ =>
              false
          }
      }

    ) crawlAll
  }

}

object RunCrawler extends App {
  HtmlCrawler.crawlAll
}