package kornell.core.entity;

import com.google.web.bindery.autobean.shared.AutoBean;


public class EnrollmentCategory {
	
	public static boolean isFinished(AutoBean<Enrollment> instance) {
		Enrollment enrollment = instance.as();
		return enrollment.getCertifiedAt() != null;
	}
	
}