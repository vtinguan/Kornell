package kornell.core.error;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface KornellError {
	String getCode();
	void setCode(String code);
	
	Map<String,String> getParams();
	void setParams(Map<String,String> params);
	
	List<String> getStuffs();
	void setStuffs(List<String> stuffs);
	
	Integer getUala();
	void setUala(Integer uala);
	
}

