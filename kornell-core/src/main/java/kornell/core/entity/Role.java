package kornell.core.entity;

public interface Role {
	RoleType getRoleType();
	void setRoleType(RoleType roleType);
	
	UserRole getUserRole();
	void setUserRole(UserRole userRole);
	
	DeanRole getDeanRole();
	void setDeanRole(DeanRole deanRole);
}
