package kornell.core.to;

import kornell.core.error.KornellErrorTO;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface TOFactory  extends AutoBeanFactory {
	public static String PREFIX = "application/vnd.kornell.v1.to.";
	AutoBean<CoursesTO> newCoursesTO();
	AutoBean<CourseVersionsTO> newCourseVersionsTO();
	AutoBean<CourseClassTO> newCourseClassTO();
	AutoBean<CourseClassesTO> newCourseClassesTO();
	AutoBean<CourseVersionTO> newCourseVersionTO();
	AutoBean<UserInfoTO> newUserInfoTO();
	AutoBean<UserHelloTO> newUserHelloTO();
	AutoBean<EnrollmentTO> newEnrollmentTO();
	AutoBean<EnrollmentsTO> newEnrollmentsTO();
	AutoBean<RegistrationRequestTO> newRegistrationRequestTO();
	AutoBean<EnrollmentRequestTO> newEnrollmentRequestTO();
	AutoBean<EnrollmentRequestsTO> newEnrollmentRequestsTO();
	AutoBean<RoleTO> newRoleTO();
	AutoBean<RolesTO> newRolesTO();
	AutoBean<LibraryFileTO> newLibraryFileTO();
	AutoBean<LibraryFilesTO> newLibraryFilesTO();
	AutoBean<UnreadChatThreadTO> newUnreadChatThreadTO();
	AutoBean<UnreadChatThreadsTO> newUnreadChatThreadsTO();
	AutoBean<ChatThreadMessageTO> newChatThreadMessageTO();
	AutoBean<ChatThreadMessagesTO> newChatThreadMessagesTO();
	AutoBean<InstitutionRegistrationPrefixesTO> newInstitutionRegistrationPrefixesTO();
	AutoBean<InstitutionHostNamesTO> newInstitutionHostNamesTO();
	AutoBean<InstitutionEmailWhitelistTO> newInstitutionEmailWhitelistTO();
	AutoBean<KornellErrorTO> newKornellErrorTO();
	AutoBean<PersonTO> newPersonTO();
	AutoBean<PeopleTO> newPeopleTO();
	AutoBean<TokenTO> newTokenTO();
	AutoBean<SimplePersonTO> newSimplePersonTO();
	AutoBean<SimplePeopleTO> newSimplePeopleTO();
	AutoBean<EnrollmentLaunchTO> newEnrollmentLaunchTO();
	AutoBean<EntityChangedEventsTO> newEntityChangedEventsTO();
}
