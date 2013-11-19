package model

import scala.slick.driver.PostgresDriver.simple._


object Regions extends Table[(String, String, Option[Double], Option[Double])]("regions") {

  def iso = column[String]("iso")

  def name = column[String]("name")

  def lat = column[Option[Double]]("lat")

  def lng = column[Option[Double]]("lng")

  def * = iso ~ name ~ lat ~ lng
}
