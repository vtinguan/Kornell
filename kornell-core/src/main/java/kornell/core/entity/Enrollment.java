package kornell.core.entity;

import java.math.BigDecimal;
import java.util.Date;

public interface Enrollment {
	public static String TYPE = EntityFactory.PREFIX + "enrollment+json";
	
	String getUUID();
	void setUUID(String uuid);
	
	Date getEnrolledOn();
	void setEnrolledOn(Date enrolledOn);
	
	String getCourseClassUUID();
	void setCourseClassUUID(String courseClassUUID);
	
	Person getPerson();
	void setPerson(Person person);
	
	/**
	 * @return Progress on the course as a percentage (between 0 and 1) 
	 */
	BigDecimal getProgress(); 
	void setProgress(BigDecimal progress);
	
	String getNotes();
	void setNotes(String notes);
	
	String getLastActomVisited();
	void setLastActomVisited(String lastActomVisited);
	
	EnrollmentState getState();
	void setState(EnrollmentState state);
}
