package kornell.server.repository.s3

class S3 {
  def actoms = List("http://...")
}

object S3 {
  def apply(repository_uuid:String) = new S3()
}