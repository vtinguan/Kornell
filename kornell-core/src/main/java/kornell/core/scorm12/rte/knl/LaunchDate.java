package kornell.core.scorm12.rte.knl;

import java.util.Map;

import kornell.core.scorm12.rte.DMElement;
import kornell.core.scorm12.rte.DataType;
import kornell.core.scorm12.rte.SCOAccess;
import static kornell.core.util.StringUtils.*;

public class LaunchDate extends DMElement{
	public static final LaunchDate dme = new LaunchDate();
	
	public LaunchDate() {
		super("launch_date",false, DataType.CMITimespan, SCOAccess.RO);
	}
	
	public Map<String, String> initializeMap(Map<String, String> entries) {
		return set(isoNow());
	}
}