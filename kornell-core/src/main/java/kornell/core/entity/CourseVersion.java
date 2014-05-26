package kornell.core.entity;

import java.util.Date;

public interface CourseVersion extends Named {
	String getCourseUUID();
	void setCourseUUID(String courseUUID);
	
	String getRepositoryUUID();
	void setRepositoryUUID(String repositoryUUID);
	
	String getDistributionPrefix();
	void setDistributionPrefix(String distributionPrefix);
	
	Date getVersionCreatedAt();
	void setVersionCreatedAt(Date versionCreatedAt);
	
	ContentSpec getContentSpec();
	void setContentSpec(ContentSpec contentSpec);
	
	boolean isDisabled();
	void setDisabled(boolean disabled);
	
}
