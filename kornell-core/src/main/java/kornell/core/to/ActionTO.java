package kornell.core.to;

import java.util.Map;

public interface ActionTO {
	public static final String TYPE = TOFactory.PREFIX + "action+json";
	
	ActionType getType();
	void setType(ActionType type);
	
	Map<String, String> getProperties();
	void setProperties(Map<String,String> properties);
}
