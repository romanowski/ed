import slick.session.{Database, Session}


/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 21.10.13
 * Time: 18:49
 * To change this template use File | Settings | File Templates.
 */
object DatabaseConf {

  //TODO
  def db: Database = Database.forURL("jdbc:postgresql://localhost/trip_data?user=xxx&password=changeme", driver = "org.postgresql.Driver")

  }
