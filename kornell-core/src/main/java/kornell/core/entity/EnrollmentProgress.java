package kornell.core.entity;

import java.util.Date;

public interface EnrollmentProgress {
	Integer getProgress();
	void setProgress(Integer progress);
	
	EnrollmentProgressDescription getDescription();
	void setDescription(EnrollmentProgressDescription description);
	
	String getCertifiedAt();
	void setCertifiedAt(String certifiedAt);
}
