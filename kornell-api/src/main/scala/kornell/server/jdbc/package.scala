package kornell.server

package object jdbc {
  def prop(name: String) = System.getProperty(name)

  val DEFAULT_URL = "jdbc:mysql:///ebdb"
  val DEFAULT_USERNAME= "kornell"
  val DEFAULT_PASSWORD= "42kornell73"

}