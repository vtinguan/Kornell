package kornell.server.jdbc.repository

import kornell.core.entity.Enrollment
import kornell.core.entity.EnrollmentState
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities._
import kornell.core.entity.Enrollments
import kornell.core.to.EnrollmentTO
import kornell.core.to.EnrollmentsTO
import kornell.server.repository.TOs
import kornell.core.error.exception.EntityConflictException
import scala.collection.mutable.Buffer
import kornell.core.to.SimplePersonTO
import kornell.core.to.SimplePeopleTO
import kornell.server.repository.Entities
import kornell.core.entity.Person

object EnrollmentsRepo {

  def byCourseClass(courseClassUUID: String) =
    byCourseClassPaged(courseClassUUID, "", Int.MaxValue, 1)

  def byCourseClassPaged(courseClassUUID: String, searchTerm: String, pageSize: Int, pageNumber: Int) = {
    val resultOffset = (pageNumber.max(1) - 1) * pageSize
    val filteredSearchTerm = '%' + Option(searchTerm).getOrElse("") + '%'

    val enrollmentsTO = TOs.newEnrollmentsTO(
      sql"""
			  select 
      		e.*, 
      		p.uuid as personUUID,
      		p.fullName,
      		if(pw.username is not null, pw.username, p.email) as username
				from Enrollment e 
				join Person p on e.person_uuid = p.uuid
				left join Password pw on p.uuid = pw.person_uuid
				where e.class_uuid = ${courseClassUUID} and
                (p.fullName like ${filteredSearchTerm}
                or pw.username like ${filteredSearchTerm}
                or p.email like ${filteredSearchTerm})
				order by e.state desc, p.fullName, username limit ${resultOffset}, ${pageSize}
			    """.map[EnrollmentTO](toEnrollmentTO))
    enrollmentsTO.setCount(
        sql"""select count(*) from Enrollment e where e.class_uuid = ${courseClassUUID}""".first[String].get.toInt)
    enrollmentsTO.setCountCancelled(
      sql"""select count(*) from Enrollment e where e.class_uuid = ${courseClassUUID}
            and state = ${EnrollmentState.cancelled.toString}""".first[String].get.toInt)
    enrollmentsTO.setPageSize(pageSize)
    enrollmentsTO.setPageNumber(pageNumber.max(1))
    enrollmentsTO.setSearchCount({
      if (searchTerm == "")
        0
      else
        sql"""
          select count(*), 
          if(pw.username is not null, pw.username, p.email) as username
          from Enrollment e 
          join Person p on e.person_uuid = p.uuid
          left join Password pw on p.uuid = pw.person_uuid
          where e.class_uuid = ${courseClassUUID} and 
          (p.fullName like ${filteredSearchTerm}
          or pw.username like ${filteredSearchTerm}
          or p.email like ${filteredSearchTerm})
        """.first[String].get.toInt
    })
    enrollmentsTO
  }

  def byPerson(personUUID: String) =
    sql"""
    SELECT 
    	e.*
	  FROM Enrollment e join Person p on e.person_uuid = p.uuid 
    WHERE e.person_uuid = ${personUUID}
	    """.map[Enrollment](toEnrollment)

  def byCourseClassAndPerson(courseClassUUID: String, personUUID: String): Option[Enrollment] =
    sql"""
	  SELECT e.*, p.* 
    FROM Enrollment e join Person p on e.person_uuid = p.uuid
    WHERE e.class_uuid = ${courseClassUUID} 
	    AND e.person_uuid = ${personUUID}
	    """.first[Enrollment]

  def byCourseClassAndUsername(courseClassUUID: String, username: String): Option[String] =
    sql"""
	  SELECT e.uuid
    FROM Enrollment e join Person p on e.person_uuid = p.uuid
	join Password pw on pw.person_uuid = p.uuid
    WHERE e.class_uuid = ${courseClassUUID} 
	    AND pw.username = ${username}
	    """.first[String]

  def byCourseVersionAndPerson(courseVersionUUID: String, personUUID: String): Option[Enrollment] =
    sql"""
    	SELECT e.*, p.* 
    FROM Enrollment e join Person p on e.person_uuid = p.uuid
    WHERE e.courseVersionUUID = ${courseVersionUUID} 
	    AND e.person_uuid = ${personUUID}
	    """.first[Enrollment]

  def byStateAndPerson(state: EnrollmentState, personUUID: String) =
    sql"""
	  SELECT e.*, p.* 
    FROM Enrollment e join Person p on e.person_uuid = p.uuid
    WHERE e.person_uuid = ${personUUID}
	    AND e.state = ${state.toString()}
    ORDER BY e.state desc, p.fullName, p.email
	    """.map[EnrollmentTO](toEnrollmentTO)

  def create(
        courseClassUUID:String,
        personUUID:String,
        enrollmentState:EnrollmentState, 
        courseVersionUUID:String,
        parentEnrollmentUUID:String):Enrollment = 
     create(Entities.newEnrollment(null, null, courseClassUUID, personUUID, null, "", EnrollmentState.notEnrolled, null, null, null, null, null, courseVersionUUID,parentEnrollmentUUID))

      
  def create(enrollment: Enrollment):Enrollment = {
    if (enrollment.getUUID == null)
      enrollment.setUUID(randomUUID)
    if (enrollment.getCourseClassUUID != null && enrollment.getCourseVersionUUID != null)
      throw new EntityConflictException("doubleEnrollmentCriteria")
    sql""" 
    	insert into Enrollment(uuid,class_uuid,person_uuid,enrolledOn,state,courseVersionUUID,parentEnrollmentUUID)
    	values(
    		${enrollment.getUUID},
    		${enrollment.getCourseClassUUID},
    		${enrollment.getPersonUUID}, 
    		now(),
    		${enrollment.getState.toString},
    		${enrollment.getCourseVersionUUID},
        	${enrollment.getParentEnrollmentUUID}
    	)""".executeUpdate
    enrollment
  }

  def find(person: PersonRepo, course_uuid: String): Enrollment = sql"""
	  select * from Enrollment 
	  where person_uuid=${person.uuid}
	   and course_uuid=${course_uuid}"""
    .first[Enrollment]
    .get

  def simplePersonList(courseClassUUID: String): SimplePeopleTO = {
    TOs.newSimplePeopleTO(sql"""select p.uuid as uuid, p.fullName as fullName, pw.username as username
    from Enrollment enr 
    join Person p on enr.person_uuid = p.uuid
    left join Password pw on p.uuid = pw.person_uuid
    where enr.state <> ${EnrollmentState.cancelled.toString} and
    enr.class_uuid = ${courseClassUUID}""".map[SimplePersonTO](toSimplePersonTO))
  }
    
  def getEspinafreEmailList(): List[Person] = {
    sql"""select p.* from Enrollment e 
        join CourseVersion cv on cv.uuid = e.courseVersionUUID
    	join Person p on e.person_uuid = p.uuid
        where cv.label = 'espinafre'
    	and p.receiveEmailCommunication = 1
    	and e.end_date = concat(curdate(), ' 23:59:59')
    	and e.progress < 100
    """.map[Person](toPerson)
  }
}
