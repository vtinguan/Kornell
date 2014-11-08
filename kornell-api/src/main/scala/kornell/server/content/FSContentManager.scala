package kornell.server.content

import java.io.FileInputStream
import java.nio.file.Paths
import java.io.InputStream
import kornell.core.entity.ContentStore
import kornell.core.util.StringUtils._

class FSContentManager(cs:ContentStore) extends ContentManager{
  //TODO: Read from ContentStore
  /* UCC
  val root = "/Users/faermanj/Dropbox (Craftware)/Content/unicc";
  val prefix = "/repository/42df235e-a2e8-455b-b341-84b4f8e5c88b/";
  val distPrefix = "vcnr1720140730" */
  
  /* Midway */
  val root = "/Users/faermanj/Dropbox (Craftware)/Content/midway";
  val prefix = "/repository/840e93aa-2373-4fb5-ba4a-999bb3f43888/";
  val distPrefix = "suplementacao-alimentar/v0.3" 
  
  override def getObjectStream(obj:String):InputStream = { 
  	val file = Paths.get(root, prefix, distPrefix, obj).toFile();
  	new FileInputStream(file)
  }
  
	override def getURL(obj:String) = composeURL(prefix,distPrefix,obj) 
}