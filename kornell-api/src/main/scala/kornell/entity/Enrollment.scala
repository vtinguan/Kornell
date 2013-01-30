package kornell.entity

import javax.persistence.Column
import javax.persistence.Id
import scala.reflect.BeanProperty

class Enrollment(_uuid:String) {
  @BeanProperty
  @Id
  var uuid:String = _uuid
  
  @BeanProperty
  @Column(unique=true)
  var code:String = ""
  
  @BeanProperty
  var packageURL:String = ""
}