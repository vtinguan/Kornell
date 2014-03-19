package kornell.core.entity;

public interface Role {
	public static String TYPE = EntityFactory.PREFIX + "role+json";
	
	RoleType getRoleType();
	void setRoleType(RoleType roleType);
	
	String getUsername();
	void setUsername(String username);
	
	UserRole getUserRole();
	void setUserRole(UserRole userRole);
	
	PlatformAdminRole getPlatformAdminRole();
	void setPlatformAdminRole(PlatformAdminRole platformRole);
	
	InstitutionAdminRole getInstitutionAdminRole();
	void setInstitutionAdminRole(InstitutionAdminRole institutionAdminRole);
	
	CourseClassAdminRole getCourseClassAdminRole();
	void setCourseClassAdminRole(CourseClassAdminRole courseClassAdminRole);
}
