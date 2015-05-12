package kornell.core.scorm12.rte.knl;

import java.util.Map;

import kornell.core.scorm12.rte.DMElement;
import kornell.core.scorm12.rte.DataType;
import kornell.core.scorm12.rte.SCOAccess;
import kornell.core.util.StringUtils;

public class FirstLaunch extends DMElement{
	public static final FirstLaunch dme = new FirstLaunch();
	
	public FirstLaunch() {
		super("first_launch",false, DataType.CMIString255, SCOAccess.RO);
	}
	
	public Map<String, String> initializeMap(Map<String, String> entries) {
		return defaultTo(entries, defaultValue());
	}

	private String defaultValue() {
		return StringUtils.isoNow();
	}

}
