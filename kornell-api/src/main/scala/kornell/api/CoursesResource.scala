package kornell.api

import javax.ws.rs.Produces
import javax.ws.rs.Path
import scala.collection.JavaConverters._
import javax.ws.rs.GET
import javax.inject.Inject
import javax.persistence.EntityManager
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.shared.to.TOFactory
import java.util.UUID
import java.util.Date
import com.google.web.bindery.autobean.shared.AutoBeanCodex
import com.google.web.bindery.autobean.shared.AutoBeanUtils
import kornell.core.shared.to.CoursesTO
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.entity.Enrollment
import kornell.entity.Course
import scala.collection.mutable.MutableList
import kornell.core.shared.to.CourseTO
import java.util.ArrayList

@Produces(Array(CoursesTO.MIME_TYPE))
@Path("courses")
class CoursesResource @Inject() (
  val em: EntityManager,
  val toFactory: TOFactory) {
  
  def this() = this(null, null)

  @GET
  def getCourses(@Context sc: SecurityContext) = {
    val username = sc.getUserPrincipal.getName

    val person = em.createNamedQuery("person.byUsername")
      .setParameter("username", username)
      .setMaxResults(1)
      .getSingleResult
      
    val courses = em
      .createQuery("select c from Course c")
      .getResultList
      .asScala
      .map(c => c.asInstanceOf[Course])
      
    val enrollmentsMap = em
      .createQuery("select e from Enrollment e where e.person = :person")
      .setParameter("person", person)
      .getResultList
      .asScala
      .map(e => e.asInstanceOf[Enrollment])
      .groupBy(_.getCourse)
      
    
    val coursesTO = toFactory.coursesTO().as()
    coursesTO.setCourses(new ArrayList)
    
    for(course <- courses){    	
    	val enrollments = enrollmentsMap.get(course)
    	val courseTO = toFactory.courseTO().as()
    	
    	courseTO.setCourseDescription(course.getDescription)
    	courseTO.setCourseUUID(course.getUuid)
    	courseTO.setPackageURL(course.getPackageURL)

    	if(enrollments.isDefined)
    		courseTO.setEnrollmentDate(enrollments.get.head.getEnrolledOn)
    	coursesTO.getCourses().add(courseTO)

    }


    

    coursesTO
  }
}