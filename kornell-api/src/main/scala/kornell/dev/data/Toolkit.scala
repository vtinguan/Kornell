package kornell.dev.data

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.slick.jdbc.{StaticQuery => Q}
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.session.Database.threadLocalSession

import kornell.dev.SQLFinder
import kornell.repository.SlickRepository


trait Toolkit extends SlickRepository {
  def dropDB = nodb withSession sqlu"drop database if exists ebdb".execute 
  def createDB = nodb withSession sqlu"create database if not exists ebdb".execute
  //TODO drop first or add 'if not exists'
  
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