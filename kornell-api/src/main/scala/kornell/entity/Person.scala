package kornell.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType._
import javax.persistence.Id
import scala.reflect.BeanProperty
import java.lang.Integer
import javax.persistence.NamedQuery

@Entity
@NamedQuery(name="person.byUsername",query="select p.person from Principal p where p.username=:username")
class Person(_fullName:String) {
  @BeanProperty
  @Id @GeneratedValue(strategy = IDENTITY)
  var id: Integer = null
  
  @BeanProperty
  var fullName:String = _fullName
  
  def this() = this(null)
  
}