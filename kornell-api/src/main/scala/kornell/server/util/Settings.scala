package kornell.server.util

import io.Source._
import java.util.Properties

object Settings {

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