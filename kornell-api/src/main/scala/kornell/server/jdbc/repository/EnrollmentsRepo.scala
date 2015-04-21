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

object EnrollmentsRepo {

  def byCourseClass(courseClassUUID: String) =
    byCourseClassPaged(courseClassUUID, Int.MaxValue, 1)
 
  def byCourseClassPaged(courseClassUUID: String, pageSize: Int, pageNumber: Int) = {
    val resultOffset = (pageNumber.max(1) - 1) * pageSize
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
				where e.class_uuid = ${courseClassUUID}
				order by e.state desc, p.fullName, pw.username limit ${resultOffset}, ${pageSize}
			    """.map[EnrollmentTO](toEnrollmentTO))
    enrollmentsTO.setCount(
        sql"""select count(*) from Enrollment e where e.class_uuid = ${courseClassUUID}""".first[String].get.toInt)
    enrollmentsTO.setCountCancelled(
        sql"""select count(*) from Enrollment e where e.class_uuid = ${courseClassUUID}
            and state = ${EnrollmentState.cancelled.toString}""".first[String].get.toInt)
    enrollmentsTO.setPageSize(pageSize)
    enrollmentsTO.setPageNumber(resultOffset)
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

  def create(enrollment: Enrollment) = {
    if (enrollment.getUUID == null)
      enrollment.setUUID(randomUUID)
    if(enrollment.getCourseClassUUID != null && enrollment.getCourseVersionUUID != null)
      throw new EntityConflictException("doubleEnrollmentCriteria")
    sql""" 
    	insert into Enrollment(uuid,class_uuid,person_uuid,enrolledOn,state,courseVersionUUID)
    	values(
    		${enrollment.getUUID},
    		${enrollment.getCourseClassUUID},
    		${enrollment.getPersonUUID}, 
    		now(),
    		${enrollment.getState.toString},
    		${enrollment.getCourseVersionUUID}
    	)""".executeUpdate
    enrollment
  }

  def find(person: PersonRepo, course_uuid: String): Enrollment = sql"""
	  select * from Enrollment 
	  where person_uuid=${person.uuid}
	   and course_uuid=${course_uuid}"""
    .first[Enrollment]
    .get
}
