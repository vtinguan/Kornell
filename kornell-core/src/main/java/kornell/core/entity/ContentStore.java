package kornell.core.entity;
import java.util.Map;

public interface ContentStore {
	String getUUID();
	void setUUID(String UUID);

	Map<String,String> getProperties();
	void setProperties(Map<String,String> properties);
}
