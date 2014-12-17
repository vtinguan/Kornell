package kornell.server.util

trait Identifiable {
  var uuid:String = null
  
  def withUUID(uuid:String) : this.type = {
   if (uuid != null) throw new IllegalStateException("The repository identity is immutable.")
   this.uuid = uuid
   this
  }
  
}