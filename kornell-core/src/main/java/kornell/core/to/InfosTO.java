package kornell.core.to;

import java.util.List;
import java.util.Map;

public interface InfosTO {
	public static final String TYPE = TOFactory.PREFIX + "infos+json"; 
	
	Map<String,List<InfoTO>> getInfoTOs();
	void setInfoTOs(Map<String,List<InfoTO>> infoTOs);
}
