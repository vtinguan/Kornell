package kornell.core.shared.data;

import java.math.BigDecimal;
import java.util.Date;

public interface Enrollment {
	String getUUID();
	void setUUID(String uuid);
	Date getEnrolledOn();
	void setEnrolledOn(Date enrolledOn);
	String getCourseUUID();
	void setCourseUUID(String courseUUID);
	String getPersonUUID();
	void setPersonUUID(String personUUID);	
	/**
	 * @return Progress on the course as a percentage (between 0 and 1) 
	 */
	BigDecimal getProgress(); 
	void setProgress(BigDecimal progress);
	String getNotes();
	void setNotes(String notes);	
}
