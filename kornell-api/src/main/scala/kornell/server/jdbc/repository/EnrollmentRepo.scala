package kornell.server.jdbc.repository

import kornell.core.entity.Enrollment
import java.sql.ResultSet
import kornell.server.repository.Entities
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities._
import kornell.core.entity.Enrollment
import kornell.core.entity.EnrollmentState
import kornell.core.entity.ContentSpec._
import kornell.core.entity.Person
import kornell.server.repository.ContentRepository
import kornell.core.lom.ContentsOps
import scala.collection.JavaConverters._
import kornell.core.entity.ActomEntries

class EnrollmentRepo(enrollmentUUID: String) {
  lazy val finder = sql"""
  select e.uuid, e.enrolledOn, e.class_uuid,
  e.person_uuid, e.progress, e.notes, e.state, e.lastProgressUpdate  
  from Enrollment e where uuid = ${enrollmentUUID} 
"""

  def get: Enrollment = finder.get[Enrollment]

  def first: Option[Enrollment] =
    finder.first[Enrollment]

  def update(enrollment: Enrollment): Enrollment = {
    val progress = enrollment.getProgress
    sql"""
    | update Enrollment e
    | set e.enrolledOn = ${enrollment.getEnrolledOn},
    | e.class_uuid = ${enrollment.getCourseClassUUID},
    | e.person_uuid = ${enrollment.getPerson.getUUID},
    | e.progress = ${enrollment.getProgress},
    | e.notes = ${enrollment.getNotes},
    | e.state = ${enrollment.getState.toString},
    | e.lastProgressUpdate = ${enrollment.getLastProgressUpdate}
    | where e.uuid = ${enrollment.getUUID}""".executeUpdate
    enrollment
  }

  def findGrades: List[String] = sql"""
    	select * from ActomEntries
    	where enrollment_uuid = ${enrollmentUUID}
    	and entryKey = 'cmi.core.score.raw'
    """.map { rs => rs.getString("entryValue") }

  def updateProgress = for {
    e <- first
    cc <- CourseClassesRepo(e.getCourseClassUUID).first
    cv <- CourseVersionRepo(cc.getCourseVersionUUID).first
  } cv.getContentSpec match {
    case KNL => updateKNLProgress(e)
    case SCORM12 => updateSCORM12Progress(e)
  }

  def updateKNLProgress(e: Enrollment) = {
    val contents = ContentRepository.findKNLVisitedContent(e.getCourseClassUUID, e.getPerson())
    val actoms = ContentsOps.collectActoms(contents).asScala
    val visited = actoms.filter(_.isVisited).size
    val newProgress = visited / actoms.size.toDouble
    val newProgressPerc = (newProgress * 100).floor.toInt
    setEnrollmentProgress(e, newProgressPerc)
  }

  def findProgressMilestone(e: Enrollment, actomKey: String): Option[Int] =
    try {
      val actomLike = "%" + actomKey
      val enrollmentUUID = e.getUUID
      val progress = sql"""
	  select progress from ActomEntries AE
		join ProgressMilestone PM on AE.actomKey = PM.actomKey and AE.entryValue = PM.entryValue
	where AE.enrollment_uuid = ${enrollmentUUID}
		and AE.actomKey LIKE ${actomLike}
		and AE.entryKey = 'cmi.core.lesson_location'
  """.map[Integer] { rs => rs.getInt("progress") }

      if (progress.isEmpty) None else Some(progress.head)
    }

  def updateSCORM12Progress(e: Enrollment) = {
    //TODO: Consider lesson_status
    val actomKeys = ContentRepository.findSCORM12Actoms(e.getCourseClassUUID)
    val progresses = actomKeys
      .flatMap { actomKey => findProgressMilestone(e, actomKey) }
    val progress = progresses.foldLeft(1)(_ max _)

    setEnrollmentProgress(e, progress)
  }

  def setEnrollmentProgress(e: Enrollment, newProgress: Int) = {
    val currentProgress = e.getProgress
    val isProgress = newProgress > currentProgress
    val isValid = newProgress >= 0 && newProgress <= 100    
    if (isValid && isProgress) {
      e.setProgress(newProgress)
      update(e)
    } else {
      logger.warning(s"Invalid progress [${currentProgress} to ${newProgress}] on enrollment [${e.getUUID}]")
    }
  }
}

object EnrollmentRepo {
  def apply(uuid: String) = new EnrollmentRepo(uuid)
}