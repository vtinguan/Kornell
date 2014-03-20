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

  private def fromProperties(implicit key: String) = properties map { _.getProperty(key) }
  private def fromSystem(implicit key: String) = Option(System.getProperty(key))
  private def fromEnv(implicit key: String) = Option(System.getenv(key))

}