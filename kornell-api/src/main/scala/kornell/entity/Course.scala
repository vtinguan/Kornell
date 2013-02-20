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
  var uuid:String = _
  
  @BeanProperty
  @Column(unique=true)
  var code:String = _
  
  @BeanProperty
  var packageURL:String = _
  
  @BeanProperty
  @Column(length=1024)
  var description:String = _
  
  override def toString = "Course["+code+"]"
  
  override def hashCode = code.hashCode
  override def equals(other:Any) = other.asInstanceOf[Course].getCode.equals(code)
}