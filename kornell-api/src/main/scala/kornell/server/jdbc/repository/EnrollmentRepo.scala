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
				certifiedAt = ${e.getCertifiedAt}
      where uuid = ${e.getUUID} """.executeUpdate
    e
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
      e.setLastProgressUpdate(new Date)
      e.setProgress(newProgress)
      update(e)
      checkCompletion(e);
    } else {
      logger.warning(s"Invalid progress [${currentProgress} to ${newProgress}] on enrollment [${e.getUUID}]")
    }
  }

  //TODO: wrong impl: should be across all grades
  def maxGradetoDec(rs: ResultSet): BigDecimal = rs.getBigDecimal("maxScore")
  def findMaxScore(enrollmentUUID: String):Option[BigDecimal] = sql"""
  		SELECT  MAX(CAST(entryValue AS DECIMAL(7,5))) as maxScore
  		FROM ActomEntries
  		WHERE enrollment_uuid = ${enrollmentUUID}
  		AND entryKey = 'cmi.core.score.raw'
  """.first(maxGradetoDec)

  def updateAssessment = first map { e =>
    val cc = first.flatMap { e => CourseClassRepo(e.getCourseClassUUID).first }
    val requiredScore: Option[BigDecimal] = cc.map { _.getRequiredScore }
    requiredScore map { reqScore =>
      val maxScore = findMaxScore(e.getUUID)
      maxScore.map { mScore =>
        if (! Assessment.PASSED.equals(e.getAssessment())) {
          val assessment = if (mScore.compareTo(reqScore) >= 0)
            Assessment.PASSED
          else
            Assessment.FAILED
          e.setAssessmentScore(mScore)
          e.setAssessment(assessment)
          e.setLastAssessmentUpdate(new Date)
          update(e)
          checkCompletion(e)
        }
      }
    }
  }
  
  def checkCompletion(e:Enrollment) = {
    val isPassed = Assessment.PASSED == e.getAssessment
    val isCompleted = e.getProgress() == 100
    val isUncertified = e.getCertifiedAt() == null
    if( isPassed
        && isCompleted 
        && isUncertified ){
      e.setCertifiedAt(new Date)
      update(e)
    }
  }

}

object EnrollmentRepo {
  def apply(uuid: String) = new EnrollmentRepo(uuid)
}