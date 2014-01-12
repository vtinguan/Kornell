package kornell.server.util

object Settings {
  def get(key: String): Option[String] =
    Option(System.getProperty(key))
      .orElse(Option(System.getenv(key)))

}