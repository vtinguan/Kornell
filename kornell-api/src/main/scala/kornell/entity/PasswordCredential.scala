package kornell.entity

import javax.persistence.GeneratedValue
import javax.persistence.Id
import scala.reflect.BeanProperty
import java.io.Serializable
import java.lang.Integer
import javax.persistence.Entity
import javax.persistence.Column
import javax.persistence.GenerationType._
import javax.persistence.OneToOne

@Entity
class PasswordCredential(_principal:Principal,_password:String) 
	extends Serializable {

  @BeanProperty
  //TODO: I'd love to use strategy=AUTO, but it creates the hibernate_sequence table...
  @Id @GeneratedValue(strategy=IDENTITY)
  var id:Integer = null
  
  @BeanProperty
  @OneToOne
  var principal:Principal = _principal
  
  @BeanProperty
  var password:String = _password
  
  def this() = this(null,null)
}