package kornell.core.to;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface TOFactory  extends AutoBeanFactory {
	public static String PREFIX = "application/vnd.kornell.v1.to.";
	AutoBean<CourseTO> newCourseTO();
	AutoBean<CoursesTO> newCoursesTO();
	AutoBean<CourseClassTO> newCourseClassTO();
	AutoBean<CourseClassesTO> newCourseClassesTO();
	AutoBean<CourseVersionTO> newCourseVersionTO();
	AutoBean<UserInfoTO> newUserInfoTO();
	AutoBean<RegistrationsTO> newRegistrationsTO();
	AutoBean<RegistrationRequestTO> newRegistrationRequestTO();
	AutoBean<EnrollmentRequestTO> newEnrollmentRequestTO();
	AutoBean<EnrollmentRequestsTO> newEnrollmentRequestsTO();
	AutoBean<S3PolicyTO> newS3PolicyTO();
}
