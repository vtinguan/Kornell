package kornell.core.entity;

import java.util.Date;

public interface ChatThread extends Entity {
	public static String TYPE = EntityFactory.PREFIX + "chatThread+json";
	
	Date getCreatedAt();
	void setCreatedAt(Date createdAt);

	String getInstitutionUUID();
	void setInstitutionUUID(String institutionUUID);
	
}
