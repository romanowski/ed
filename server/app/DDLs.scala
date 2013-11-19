import model._

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 29.10.13
 * Time: 06:58
 * To change this template use File | Settings | File Templates.
 */
object DDLs extends App {

  Seq(Countries.ddl, Hotels.ddl, Users.ddl, Reviews.ddl, Regions.ddl).foreach {
    d => println(d.createStatements.mkString("", "", ";\n"))
  }

}
