package kornell.core.shared.to;

import java.util.Date;

public interface CourseTO {
	public static final String MIME_TYPE = "application/vnd.kornell.v1.to.course+json";
	
	String getCourseUUID();
	void setCourseUUID(String courseUUID);
	String getCourseDescription();
	void setCourseDescription(String courseDescription);
	String getPackageURL();
	void setPackageURL(String packageURL);
	Date getEnrollmentDate();
	void setEnrollmentDate(Date enrollmentDate);
}
