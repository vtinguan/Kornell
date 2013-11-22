package kornell.core.entity;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface EntityFactory extends AutoBeanFactory {
	public static String PREFIX = "application/vnd.kornell.v1.entity.";
	
	AutoBean<Person> newPerson();

	AutoBean<Principal> newPrincipal();

	AutoBean<Course> newCourse();

	AutoBean<Enrollment> newEnrollment();

	AutoBean<Enrollments> newEnrollments();

	AutoBean<Institution> newInstitution();

	AutoBean<Registration> newRegistration();

	AutoBean<Registrations> newRegistrations();

	AutoBean<Role> newRole();

	AutoBean<UserRole> newUserRole();

	AutoBean<DeanRole> newDeanRole();

}
