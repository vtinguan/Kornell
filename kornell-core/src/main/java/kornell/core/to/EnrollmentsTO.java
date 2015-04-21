package kornell.core.to;

import java.util.List;

public interface EnrollmentsTO {
	public static final String TYPE = TOFactory.PREFIX + "enrollments+json";
	
	List<EnrollmentTO> getEnrollmentTOs();
	void setEnrollmentTOs(List<EnrollmentTO> enrollmentTOs);
	
	Integer getCount();
	void setCount(Integer count);
	
	Integer getCountCancelled();
	void setCountCancelled(Integer countCancelled);
}
