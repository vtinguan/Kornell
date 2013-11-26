package kornell.server.repository.jdbc

class CourseClasses {
}

object CourseClasses {
  def apply(uuid:String) = CourseClassRepository(uuid);

  def byInstitution(institutionUUID: String) = ??? 
}