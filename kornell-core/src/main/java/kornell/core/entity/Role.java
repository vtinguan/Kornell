package kornell.core.entity;

public interface Role {
	RoleType getRoleType();
	void setRoleType(RoleType roleType);
	
	UserRole getUserRole();
	void setUserRole(UserRole userRole);
	
	PlatformAdminRole getPlatformAdminRole();
	void setPlatformAdminRole(PlatformAdminRole platformRole);
	
	InstitutionAdminRole getInstitutionAdminRole();
	void setInstitutionAdminRole(InstitutionAdminRole institutionAdminRole);
	
	CourseClassAdminRole getCourseClassAdminRole();
	void setCourseClassAdminRole(CourseClassAdminRole courseClassAdminRole);
}
