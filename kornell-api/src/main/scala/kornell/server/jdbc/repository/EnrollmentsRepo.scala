package kornell.server.jdbc.repository

import kornell.core.entity.Enrollment
import kornell.core.entity.EnrollmentState
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities._
import kornell.core.entity.Enrollments
import kornell.core.to.EnrollmentTO
import kornell.core.to.EnrollmentsTO
import kornell.server.repository.TOs

object EnrollmentsRepo {

  def byCourseClass(courseClassUUID: String) =
    TOs.newEnrollmentsTO(
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
				order by e.state desc, p.fullName, pw.username
			    """.map[EnrollmentTO](toEnrollmentTO))
 
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
    sql""" 
    	insert into Enrollment(uuid,class_uuid,person_uuid,enrolledOn,state)
    	values(
    		${enrollment.getUUID},
    		${enrollment.getCourseClassUUID},
    		${enrollment.getPersonUUID}, 
    		now(),
    		${enrollment.getState.toString}
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
