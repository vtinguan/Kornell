package kornell.core.entity;

import java.util.Set;


public class RoleCategory {

	public static String BIND_DEFAULT = "DEFAULT";
	public static String BIND_WITH_PERSON = "PERSON";
	
	public static boolean isValidRole(Role role, RoleType type, String institutionUUID, String courseClassUUID) {
		switch (role.getRoleType()) {
		case user:
			if (RoleType.user.equals(type))
				return true;
			break;
		case courseClassAdmin:
			if (RoleType.courseClassAdmin.equals(type)
					&& courseClassUUID != null
					&& role.getCourseClassAdminRole()
							.getCourseClassUUID()
							.equals(courseClassUUID))
				return true;
			break;
		case observer:
			if (RoleType.observer.equals(type)
					&& courseClassUUID != null
					&& role.getObserverRole()
							.getCourseClassUUID()
							.equals(courseClassUUID))
				return true;
			break;
		case tutor:
			if (RoleType.tutor.equals(type)
					&& courseClassUUID != null
					&& role.getTutorRole()
							.getCourseClassUUID()
							.equals(courseClassUUID))
				return true;
			break;
		case institutionAdmin:
			if (RoleType.institutionAdmin.equals(type)
					&& institutionUUID != null
					&& role.getInstitutionAdminRole()
							.getInstitutionUUID()
							.equals(institutionUUID))
				return true;
			break;
		case platformAdmin:
			if (RoleType.platformAdmin.equals(type)
					&& institutionUUID != null
					&& role.getPlatformAdminRole()
							.getInstitutionUUID()
							.equals(institutionUUID))
				return true;
			break;
		default:
			break;
		}
		return false;
	}
	
	public static boolean isRole(Role role, RoleType type) {
		switch (role.getRoleType()) {
		case user:
			if (RoleType.user.equals(type))
				return true;
			break;
		case courseClassAdmin:
			if (RoleType.courseClassAdmin.equals(type))
				return true;
			break;
		case observer:
			if (RoleType.observer.equals(type))
				return true;
			break;
		case tutor:
			if (RoleType.tutor.equals(type))
				return true;
			break;
		case institutionAdmin:
			if (RoleType.institutionAdmin.equals(type))
				return true;
			break;
		case platformAdmin:
			if (RoleType.platformAdmin.equals(type))
				return true;
			break;
		default:
			break;
		}
		return false;
	}
	

	public static boolean isPlatformAdmin(Set<Role> roles, String institutionUUID) {
		return isValidRole(roles, RoleType.platformAdmin, institutionUUID, null);
	}

	public static boolean isInstitutionAdmin(Set<Role> roles, String institutionUUID) {
		return isValidRole(roles, RoleType.institutionAdmin, institutionUUID, null);
	}

	public static boolean isCourseClassAdmin(Set<Role> roles, String courseClassUUID) {
		return isValidRole(roles, RoleType.courseClassAdmin, null, courseClassUUID);
	}

	public static boolean isCourseClassObserver(Set<Role> roles, String courseClassUUID) {
		return isValidRole(roles, RoleType.observer, null, courseClassUUID);
	}

	public static boolean isCourseClassTutor(Set<Role> roles, String courseClassUUID) {
		return isValidRole(roles, RoleType.tutor, null, courseClassUUID);
	}

	public static boolean isValidRole(Set<Role> roles, RoleType type, String institutionUUID, String courseClassUUID) {
		for (Role role : roles) {
			if(isValidRole(role, type, institutionUUID, courseClassUUID))
				return true;
		}
		return false;
	}

	public static boolean hasRole(Set<Role> roles, RoleType type) {
		for (Role role : roles) {
			if(RoleCategory.isRole(role, type))
				return true;
		}
		return false;
	}
	
}
