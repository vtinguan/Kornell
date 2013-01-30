package kornell.entity

import scala.reflect.BeanProperty
import java.lang.Integer
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.GenerationType._
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class Course extends Serializable{
  @BeanProperty
  @Id
  var uuid:String = null
  
  @BeanProperty
  @Column(unique=true)
  var code:String = ""
  
  @BeanProperty
  var packageURL:String = ""
	
}