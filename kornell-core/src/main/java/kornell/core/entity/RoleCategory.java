package kornell.core.entity;

public class RoleCategory {
	
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
		case institutionAdmin:
			if (RoleType.institutionAdmin.equals(type)
					&& institutionUUID != null
					&& role.getInstitutionAdminRole()
							.getInstitutionUUID()
							.equals(institutionUUID))
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
	
}
