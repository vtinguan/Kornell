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
import kornell.core.entity.Assessment
import java.util.Date
import kornell.server.ep.EnrollmentSEP
import java.math.BigDecimal
import java.math.BigDecimal._
import kornell.server.util.ServerTime
import kornell.core.entity.ChatThreadType

//TODO: Specific column names and proper sql
class EnrollmentRepo(enrollmentUUID: String) {
  lazy val finder = sql" SELECT * FROM Enrollment e WHERE uuid = ${enrollmentUUID} "

  def get: Enrollment = finder.get[Enrollment]

  def first: Option[Enrollment] =
    finder.first[Enrollment]

  def update(e: Enrollment): Enrollment = {
    sql"""
    UPDATE Enrollment    
     SET 
				enrolledOn = ${e.getEnrolledOn},
				progress = ${e.getProgress},
				notes = ${e.getNotes},
				state = ${e.getState.toString},
				lastProgressUpdate = ${e.getLastProgressUpdate},
				assessment = ${Option(e.getAssessment).map(_.toString).getOrElse(null)},
				lastAssessmentUpdate = ${e.getLastAssessmentUpdate},
				assessmentScore = ${e.getAssessmentScore},
				certifiedAt = ${e.getCertifiedAt},
        parentEnrollmentUUID = ${e.getParentEnrollmentUUID},
        start_date = ${e.getStartDate},
        end_date = ${e.getEndDate}
      where uuid = ${e.getUUID} """.executeUpdate
    e
  }

  def delete(enrollmentUUID: String) = {
    sql"""
      delete from Enrollment 
      where uuid = ${enrollmentUUID}""".executeUpdate
  }

  def findGrades: List[String] = sql"""
    	select * from ActomEntries
    	where enrollment_uuid = ${enrollmentUUID}
    	and entryKey = 'cmi.core.score.raw'
    """.map { rs => rs.getString("entryValue") }

  //TODO: Convert to map/flatmat and dedup updateAssessment
  def updateProgress = for {
    e <- first
    cc <- CourseClassesRepo(e.getCourseClassUUID).first
    cv <- CourseVersionRepo(cc.getCourseVersionUUID).first
  } cv.getContentSpec match {
    case KNL => updateKNLProgress(e)
    case SCORM12 => updateSCORM12Progress(e)
  }

  def updateKNLProgress(e: Enrollment) = {
    val contents = ContentRepository.findKNLVisitedContent(e)
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

  def updateAssessment = first map { e =>
    val notPassed = !Assessment.PASSED.equals(e.getAssessment)
    if (notPassed && e.getCourseClassUUID != null){
    	val (maxScore,assessment) = assess(e)
        e.setAssessmentScore(maxScore)
        e.setAssessment(assessment)
        //TODO: Add client timestap
        e.setLastAssessmentUpdate(ServerTime.now)
        update(e)
        checkCompletion(e)
    }
  }

  
  def assess(e:Enrollment) = {
    val cc = CourseClassRepo(e.getCourseClassUUID).get
    	val reqScore: BigDecimal = Option(cc.getRequiredScore).getOrElse(ZERO)
    	val maxScore = findMaxScore(e.getUUID).getOrElse(ZERO)
    	val assessment = if (maxScore.compareTo(reqScore) >= 0)
          Assessment.PASSED
        else
          Assessment.FAILED
        (maxScore,assessment)
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
  
  def transfer(fromCourseClassUUID: String, toCourseClassUUID: String) = {
    val enrollment = first.get
    //disable participation to global class thread for old class
    ChatThreadsRepo.disableParticipantFromCourseClassThread(enrollment)
    
    //update enrollment
    sql"""update Enrollment set class_uuid = ${toCourseClassUUID} where uuid = ${enrollmentUUID}""".executeUpdate
      
    //disable old support and tutoring threads
    sql"""update ChatThread set active = 0 where courseClassUUID = ${fromCourseClassUUID} and personUUID = ${enrollment.getPersonUUID} and threadType in  (${ChatThreadType.SUPPORT.toString}, ${ChatThreadType.TUTORING.toString})""".executeUpdate
    
    //add participation to global class thread for new class
    ChatThreadsRepo.addParticipantToCourseClassThread(enrollment)
  }


}

object EnrollmentRepo {
  def apply(uuid: String) = new EnrollmentRepo(uuid)
}