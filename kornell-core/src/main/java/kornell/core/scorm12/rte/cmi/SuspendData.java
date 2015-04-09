package kornell.core.scorm12.rte.cmi;

import java.util.Map;

import kornell.core.scorm12.rte.DMElement;

public class SuspendData extends DMElement{
	public static final SuspendData dme = new SuspendData();
	private SuspendData() {
		super("suspend_data");
	}
	
	@Override
	public Map<String, String> initializeMap(Map<String, String> entries) {
		return defaultTo(entries, "");
	}

}
