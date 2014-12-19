package kornell.server.util

trait Identifiable {
  var uuid:String = null
  
  def withUUID(uuid:String) : this.type = this.synchronized {
   if (this.uuid != null) 
     throw new IllegalStateException(s"The repository identity [${uuid}] is immutable.")
   this.uuid = uuid
   this
  }
  
}