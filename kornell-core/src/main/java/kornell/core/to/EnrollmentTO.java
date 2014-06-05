package kornell.core.to;

import kornell.core.entity.Enrollment;
import kornell.core.entity.Person;

public interface EnrollmentTO {
	public static String TYPE = TOFactory.PREFIX + "enrollment+json";
	
	Enrollment getEnrollment();
	void setEnrollment(Enrollment enrollment);
	
	Person getPerson();
	void setPerson(Person person);

}
