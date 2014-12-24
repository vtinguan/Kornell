package kornell.server.test

import scala.language._
import org.scalatest.Matchers
import org.scalatest.Inside
import org.scalatest.OptionValues
import org.scalatest.FlatSpec
import scala.util.Random
import java.util.UUID
import kornell.server.jdbc.Migration
import org.scalatest.BeforeAndAfter
import kornell.server.auth.ThreadLocalAuthenticator
import kornell.server.jdbc.SQL._
import kornell.server.helper.Generator


class UnitSpec extends FlatSpec
  with Matchers
  with OptionValues
  with Inside
  with BeforeAndAfter
  with Generator { 
  UnitSpec.init

 
}

object UnitSpec {
  lazy val init = {
    println("Initializing Unit Testing")
    setEnv()
    respawnDB()
    Migration()
  }

  def setEnv() = {
    println("Setting Environment")
    System.setProperty("JDBC_CONNECTION_STRING", "jdbc:mysql:///kornell_tests?allowMultiQueries=true")
    System.setProperty("TEST_MODE", "true")
    System.setProperty("JDBC_USERNAME", "kornell_tests")
    System.setProperty("JDBC_PASSWORD", "42kornell_tests42")
  }
  
  def respawnDB() = {    
    	println("Respawning Tests DB")
    	sql"drop database if exists kornell_tests; create database kornell_tests;".executeUpdate
  }

}

