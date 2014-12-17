package kornell.server.jdbc.repository

import scala.language.implicitConversions
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
import kornell.core.entity.Assessment
import java.util.Date
import kornell.server.ep.EnrollmentSEP
import java.math.BigDecimal
import java.math.BigDecimal._
import kornell.server.util.ServerTime
import kornell.server.repository.TOs
import kornell.core.to.CourseDetailsTO
import kornell.server.repository.ContentRepository
import kornell.server.repository.ContentRepository
import javax.inject.Inject
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class EnrollmentRepo @Inject() (
  val contentRepo: ContentRepository,
  val courseClassesRepo: CourseClassesRepo) {

  def this() = this(null, null)
  def finder(enrollmentUUID: String) = sql" SELECT * FROM Enrollment e WHERE uuid = ${enrollmentUUID} "

  def get(enrollmentUUID: String): Enrollment = finder(enrollmentUUID).get[Enrollment]

  def first(enrollmentUUID: String): Option[Enrollment] = finder(enrollmentUUID).first[Enrollment]

  def update(e: Enrollment): Enrollment = {
    sql"""
    UPDATE Enrollment    
     SET 
				enrolledOn = ${e.getEnrolledOn},
				progress = ${e.getProgress},
				notes = ${e.getNotes},
				state = ${e.getState.toString},
				lastProgressUpdate = ${e.getLastProgressUpdate},
				assessment = ${e.getAssessment},
				lastAssessmentUpdate = ${e.getLastAssessmentUpdate},
				assessmentScore = ${e.getAssessmentScore},
				certifiedAt = ${e.getCertifiedAt}
      where uuid = ${e.getUUID} """.executeUpdate
    e
  }

  def delete(enrollmentUUID: String) = {
    sql"""
      delete from Enrollment 
      where uuid = ${enrollmentUUID}""".executeUpdate
  }

  def findGrades(enrollmentUUID: String): List[String] = sql"""
    	select * from ActomEntries
    	where enrollment_uuid = ${enrollmentUUID}
    	and entryKey = 'cmi.core.score.raw'
    """.map { rs => rs.getString("entryValue") }

  def updateProgress(enrollmentUUID: String) = for {
    e <- first(enrollmentUUID: String)
    cc <- courseClassesRepo.byUUID(e.getCourseClassUUID).first
    cv <- CourseVersionRepo(cc.getCourseVersionUUID).first
  } cv.getContentSpec match {
    case KNL => updateKNLProgress(e)
    case SCORM12 => updateSCORM12Progress(e)
  }

  def updateKNLProgress(e: Enrollment) = {
    val contents = contentRepo.findKNLVisitedContent(e.getCourseClassUUID, e.getPersonUUID())
    val actoms = ContentsOps.collectActoms(contents).asScala
    val visited = actoms.filter(_.isVisited).size
    val newProgress = visited / actoms.size.toDouble
    val newProgressPerc = (newProgress * 100).floor.toInt
    setEnrollmentProgress(e, newProgressPerc)
  }

  def findProgressMilestone(e: Enrollment, actomKey: String): Option[Int] =
    {
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
    val actomKeys = contentRepo.findSCORM12Actoms(e.getCourseClassUUID)
    val progresses = actomKeys
      .flatMap { actomKey => findProgressMilestone(e, actomKey) }
    val progress = progresses.foldLeft(1)(_ max _)

    setEnrollmentProgress(e, progress)
  }

  def setEnrollmentProgress(e: Enrollment, newProgress: Int) = {
    //TODO: Consider using client timestamp
    val lastProgressUpdate = ServerTime.now
    val currentProgress = e.getProgress
    val isProgress = newProgress > currentProgress
    val isValid = newProgress >= 0 && newProgress <= 100
    if (isValid && isProgress) {
      e.setLastProgressUpdate(lastProgressUpdate)
      e.setProgress(newProgress)
      update(e)
      checkCompletion(e);
    } else {
      logger.warning(s"Invalid progress [${currentProgress} to ${newProgress}] on enrollment [${e.getUUID}]")
    }
  }

  //TODO: WRONG ASSUMPTION: Courses can have multiple assessments, should be across all grades
  def findMaxScore(enrollmentUUID: String): Option[BigDecimal] = sql"""
  		SELECT  MAX(CAST(entryValue AS DECIMAL(8,5))) as maxScore
  		FROM ActomEntries
  		WHERE enrollment_uuid = ${enrollmentUUID}
  		AND entryKey = 'cmi.core.score.raw'
  """.first[BigDecimal] { rs => rs.getBigDecimal("maxScore") }

  def updateAssessment(enrollmentUUID: String) = first(enrollmentUUID) map { e =>
    val notPassed = !Assessment.PASSED.equals(e.getAssessment)
    if (notPassed) {
      val (maxScore, assessment) = assess(e)
      e.setAssessmentScore(maxScore)
      e.setAssessment(assessment)
      //TODO: Add client timestap
      e.setLastAssessmentUpdate(ServerTime.now)
      update(e)
      checkCompletion(e)
    }
  }

  def assess(e: Enrollment) = {
    val cc = courseClassesRepo.byUUID(e.getCourseClassUUID).get
    val reqScore: BigDecimal = Option(cc.getRequiredScore).getOrElse(ZERO)
    val maxScore = findMaxScore(e.getUUID).getOrElse(ZERO)
    val assessment = if (maxScore.compareTo(reqScore) >= 0)
      Assessment.PASSED
    else
      Assessment.FAILED
    (maxScore, assessment)
  }

  def findLastEventTime(e: Enrollment) = {
    val lastActomEntered = sql"""
select max(ingestedAt) as latestEvent
from ActomEntryChangedEvent 
where 
  entryKey='cmi.core.score.raw' 
  and enrollment_uuid=${e.getUUID()}
    """
      .first[String] { rs => rs.getString("latestEvent") }
    lastActomEntered
  }

  def checkCompletion(e: Enrollment) = {
    val isPassed = Assessment.PASSED == e.getAssessment
    val isCompleted = e.getProgress() == 100
    val isUncertified = e.getCertifiedAt() == null
    if (isPassed
      && isCompleted
      && isUncertified) {
      val certifiedAt = findLastEventTime(e).getOrElse(ServerTime.now)
      e.setCertifiedAt(certifiedAt)
      update(e)
    }
  }

  def findDetails(enrollmentUUID: String): Option[CourseDetailsTO] = for {
    e <- first(enrollmentUUID)
    cc <- courseClassesRepo.byUUID(e.getCourseClassUUID).first
    cv <- CourseVersionRepo(cc.getCourseVersionUUID).first
    c <- CourseRepo(cv.getCourseUUID).first
  } yield {
    val to = TOs.newCourseDetailsTO
    to.setCourseName(c.getTitle)
    to.setCourseClassName(cc.getName)
    to.setInfosTO(InfosRepo.byCourseVersion(cv.getUUID))
    to
  }

}
