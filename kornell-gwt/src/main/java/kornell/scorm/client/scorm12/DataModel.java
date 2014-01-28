package kornell.scorm.client.scorm12;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.shared.GWT;

public class DataModel {
	static final Map<String, CMIDataElement> elements = new HashMap<String,CMIDataElement>();

	protected Map<String, String> entries = new HashMap<String, String>();

	protected static void register(CMIDataElement element){
		GWT.log("Registering ["+element.getKey()+"]");
		elements.put(element.getKey(),element);
	}
}
