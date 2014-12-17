package kornell.server.util

import scala.io.Source._ 
import java.util.Properties

object Settings {

  lazy val JNDI_ROOT = get("JNDI_ROOT").getOrElse("java:")
  lazy val JNDI_DATASOURCE = get("JNDI_DATASOURCE").getOrElse("/datasources/KornellDS")
  
  def get(implicit key: String): Option[String] =
    fromProperties orElse fromSystem orElse fromEnv

  lazy val propsStream = Option(getClass.getResourceAsStream("/kornell.properties"))
  lazy val properties = propsStream map { stream =>
    val props = new Properties
    props.load(stream)
    props
  }

  def fromProperties(implicit key: String):Option[String] = properties flatMap {props => Option(props.getProperty(key))}
  def fromSystem(implicit key: String):Option[String] = Option(System.getProperty(key))
  def fromEnv(implicit key: String):Option[String] = Option(System.getenv(key))

}