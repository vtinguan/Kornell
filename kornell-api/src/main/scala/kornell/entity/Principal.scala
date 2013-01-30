package kornell.entity

import scala.reflect.BeanProperty
import javax.persistence.Entity
import java.lang.Integer
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.GenerationType._
import java.util.List
import javax.persistence.ElementCollection
import javax.persistence.ManyToOne

@Entity
class Principal(
    _person:Person,
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
  val roles:List[String] = _roles
  
  @BeanProperty
  @ManyToOne
  var person:Person = _person
  
  def this() = this(null,null,null)
}