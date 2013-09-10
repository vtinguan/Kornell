package kornell.core.shared.data;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface BeanFactory extends AutoBeanFactory {


	/* Entity Beans */
	AutoBean<Person> newPerson();
	AutoBean<Principal> newPrincipal();
	AutoBean<Course> newCourse();
	AutoBean<Enrollment> newEnrollment();
	AutoBean<Institution> newInstitution();
	AutoBean<Registration> newRegistration();
	AutoBean<Registrations> newRegistrations();

}
