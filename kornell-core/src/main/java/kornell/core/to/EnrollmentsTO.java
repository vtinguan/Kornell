package kornell.core.to;

import java.util.List;

import kornell.core.entity.Enrollment;

public interface EnrollmentsTO {
	public static final String TYPE = TOFactory.PREFIX + "enrollments+json";
	
	List<EnrollmentTO> getEnrollmentTOs();
	void setEnrollmentTOs(List<EnrollmentTO> enrollmentTOs);
	
}
