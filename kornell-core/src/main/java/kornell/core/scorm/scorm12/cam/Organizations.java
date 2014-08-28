package kornell.core.scorm.scorm12.cam;

import java.util.List;

public interface Organizations {
	List<Organization> getOrganizationList();
	void setOrganizationList(List<Organization> organizationList);
	
	String getDefaultOrganization();
	void setDefaultOrganization(String defaultOrganization);
}
