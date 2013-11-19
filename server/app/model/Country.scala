package model

import scala.slick.driver.PostgresDriver.simple._


/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 29.10.13
 * Time: 06:55
 * To change this template use File | Settings | File Templates.
 */
case class Country(iso: String, name: String)

object Countries extends Table[Country]("countries") {


  def iso = column[String]("iso", O.PrimaryKey)
  def name = column[String]("name")

  def * = iso ~ name <>(Country.apply _, Country.unapply _)
}
