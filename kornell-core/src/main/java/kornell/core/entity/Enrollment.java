package kornell.core.entity;

import java.util.Date;

public interface Enrollment extends Entity{
	public static String TYPE = EntityFactory.PREFIX + "enrollment+json";
	
	Date getEnrolledOn();
	void setEnrolledOn(Date enrolledOn);
	
	String getCourseClassUUID();
	void setCourseClassUUID(String courseClassUUID);
	
	Person getPerson();
	void setPerson(Person person);

	Integer getProgress(); 
	void setProgress(Integer progress);
	
	String getNotes();
	void setNotes(String notes);
	
	String getLastActomVisited();
	void setLastActomVisited(String lastActomVisited);
	
	EnrollmentState getState();
	void setState(EnrollmentState state);
	
	Date getLastProgressUpdate();
	void setLastProgressUpdate(Date lastProgressUpdate);
	
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
	
	Date getLastAssessmentUpdate();
	void setLastAssessmentUpdate(Date lastAssessmentUpdate);
}
