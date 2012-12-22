package kornell.entity

import scala.reflect.BeanProperty
import javax.persistence.Entity
import java.lang.Integer
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.GenerationType._
import java.util.List
import java.util.ArrayList
import javax.persistence.ElementCollection

@Entity
class Principal(
    _username:String,
    _roles:List[String]) {
  @BeanProperty
  @Id @GeneratedValue(strategy = IDENTITY)
  var id: Integer = null

  @BeanProperty
  @Column(unique = true)
  var username: String = _username
  
  @BeanProperty
  @ElementCollection
  var roles:List[String] = _roles
  
  def this() = this(null,null)
}