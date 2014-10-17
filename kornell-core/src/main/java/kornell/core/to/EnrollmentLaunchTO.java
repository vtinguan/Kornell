package kornell.core.to;

import kornell.core.entity.CourseVersion;

public interface EnrollmentLaunchTO {
	public static final String TYPE = TOFactory.PREFIX + "enrollmentlaunch+json";

	CourseVersion getCourseVersion();
	void setCourseVersion(CourseVersion cv);
	
	CourseDetailsTO getCourseDetailsTO();
	void setCourseDetailsTO(CourseDetailsTO to);
	
	ActionTO getActionTO();
	void setActionTO(ActionTO actionTO);
}
