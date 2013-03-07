package kornell.dev

import java.nio.file.Files
import kornell.repository.slick.plain.Repository
import java.nio.charset.Charset
import java.nio.file.Path
import scala.collection.JavaConverters._
import scala.slick.jdbc.{ GetResult, StaticQuery => Q }
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.session.Database.threadLocalSession


trait Toolkit extends Repository {
  def dropDB = nodb withSession sqlu"drop database if exists kornell".execute 
  def createDB = nodb withSession sqlu"create database if not exists kornell".execute

  def truncDB = {
    dropDB
    createDB
  }

  val scripts = new SQLFinder().files
  val charset = Charset.forName("UTF-8")

  def contentOf(script: Path) = Files.readAllLines(script, charset).asScala.mkString

  def stmtsIn(script: Path) = contentOf(script) split ";"

  def execute(stmt: String) = db.withSession {
    Q.updateNA(stmt).execute
  }

  def executeContent(script: Path) = stmtsIn(script) foreach execute

  def createSchema = scripts foreach executeContent
  def respawnDB = {
    truncDB
    createSchema
  }
}