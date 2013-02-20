package kornell.entity

import javax.persistence.Column
import javax.persistence.Id
import scala.reflect.BeanProperty
import java.util.Date
import javax.persistence.OneToOne
import javax.persistence.Entity
import java.io.Serializable

@Entity
class Enrollment extends Serializable{
  @BeanProperty
  @Id
  var uuid:String = _
  
  
  @BeanProperty
  var enrolledOn:Date = _
  
  @BeanProperty
  @OneToOne
  var course:Course = _
  
  @BeanProperty
  @OneToOne
  var person:Person = _
  
  override def toString = "Enrollment["+course+","+person+","+enrolledOn+"]"
}