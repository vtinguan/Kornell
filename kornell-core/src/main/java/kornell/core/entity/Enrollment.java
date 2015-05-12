package kornell.core.entity;

import java.math.BigDecimal;
import java.util.Date;

public interface Enrollment extends Entity{
	public static String TYPE = EntityFactory.PREFIX + "enrollment+json";
	
	Date getEnrolledOn();
	void setEnrolledOn(Date enrolledOn);
	
	String getCourseClassUUID();
	void setCourseClassUUID(String courseClassUUID);
	
	String getCourseVersionUUID();
	void setCourseVersionUUID(String courseVersionUUID);
	
	String getPersonUUID();
	void setPersonUUID(String personUUID);

	Integer getProgress(); 
	void setProgress(Integer progress);
	
	String getNotes();
	void setNotes(String notes);
	
	String getLastActomVisited();
	void setLastActomVisited(String lastActomVisited);
	
	EnrollmentState getState();
	void setState(EnrollmentState state);
	
	String getLastProgressUpdate();
	void setLastProgressUpdate(String lastProgressUpdate);
	
	/**
	 * use lastProgressUpdate / lastAssessmentUpdate
	 * @return
	 */
	@Deprecated
	Date getCompletionDate();
	@Deprecated
	void setCompletionDate(Date completionDate);
	
	Assessment getAssessment();
	void setAssessment(Assessment assessment);
	
	String getLastAssessmentUpdate();
	void setLastAssessmentUpdate(String lastAssessmentUpdate);
	
	String getCertifiedAt();
	void setCertifiedAt(String certifiedAt);
	
	BigDecimal getAssessmentScore();
	void setAssessmentScore(BigDecimal assessmentScore);
	
	String getLastBilledAt();
	void setLastBilledAt(String lastBilled);

}
