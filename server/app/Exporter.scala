import io.Source
import java.io.File

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 21.10.13
 * Time: 18:11
 * To change this template use File | Settings | File Templates.
 */
object Exporter extends App {

  val Array(_, folder) = args

  val file = new File(folder)


  val Reg = "<([^>]*)>(.*)".r

  if (file.isDirectory) {
    for {name <- file.list()} {
      val child = new File(file, name)
      if (!child.isDirectory) {
        val id = name.split("_")(1).replaceAll(".dat", "")
        val (c, seq) =
          Source.fromFile(child)
            .getLines()
            .foldLeft(Map[String, String]() -> Seq[Map[String, String]]()) {
            case ((current, rest), "") => (Map(), rest :+ current)
            case ((current, rest), Reg(name, value)) => (current + (name -> value), rest)
            case (ret, str) =>
              println(str)
              ret
          }
        val all = (c +: seq).tail
        Importer.importReviews(all.tail, all.head + ("id" -> id))
      }
    }
  }
}
