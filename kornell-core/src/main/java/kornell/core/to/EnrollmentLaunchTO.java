package kornell.core.to;

import kornell.core.entity.EnrollmentEntries;
import kornell.core.lom.Contents;

public interface EnrollmentLaunchTO {
	public static final String TYPE = TOFactory.PREFIX + "enrollmentlaunchto+json";
	
	Contents getContents();
	void setContents(Contents contents);

	EnrollmentEntries getEnrollmentEntries();
	void setEnrollmentEntries(EnrollmentEntries entries);

}
