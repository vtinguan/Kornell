package kornell.core.entity;
import java.util.Map;

public interface ContentStore extends Entity{
	ContentStoreType getContentStoreType();
	void setContentStoreType(ContentStoreType contentStoreType);
	
	Map<String,String> getProperties();
	void setProperties(Map<String,String> properties);
}
