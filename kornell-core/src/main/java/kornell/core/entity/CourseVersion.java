package kornell.core.entity;

import java.util.Date;

public interface CourseVersion extends Named {
	String getCourseUUID();
	void setCourseUUID(String courseUUID);
	
	String getRepositoryUUID();
	void setRepositoryUUID(String repositoryUUID);
	
	Date getVersionCreatedAt();
	void setVersionCreatedAt(Date versionCreatedAt);
}
