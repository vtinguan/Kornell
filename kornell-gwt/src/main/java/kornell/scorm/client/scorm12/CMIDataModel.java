package kornell.scorm.client.scorm12;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.shared.GWT;

public class CMIDataModel implements CMIConstants{
	Map<String, String> values = new HashMap<String, String>();

	// Data Model Constants
	private static final String CHILDREN = "_children";
	private static final String LESSON_STATUS = "cmi.core.lesson_status";
	private static final String NOT_ATTEMPTED = "not attempted";

	boolean dirty;

	private SCORM12Adapter api;
	
	public CMIDataModel(SCORM12Adapter api){
		this.api = api;
	}

	public String getValue(String element) {
		if (element == null) {
			GWT.log("!!! Null Element");
			throw new NullPointerException(
					"Data model element can not be null.");
		}
		if (element.endsWith(CHILDREN))
			return getChildren(element);
		if (LESSON_STATUS.equals(element))
			return getLessonStatus();
		throw new IllegalArgumentException("Unkown Data Model [" + element
				+ "]");
	}

	private String getChildren(String element) {
		List<String> result = new ArrayList<String>();

		if (element.equals("cmi.core._children")) {
			result.add(LESSON_STATUS);
		}

		// TODO: Array.join
		StringBuilder buf = new StringBuilder();
		for (Iterator iterator = result.iterator(); iterator.hasNext();) {
			buf.append(iterator.next());
			if (iterator.hasNext())
				buf.append(",");
		}
		return buf.toString();
	}

	private String getLessonStatus() {
		return get(LESSON_STATUS,NOT_ATTEMPTED);
	}

	public String setValue(String param, String value) {
		values.put(param, value);
		api.onDirtyData();
		return TRUE;
	}

	public String get(String key,String deflt) {
		String value = EMPTY;
		if (values.containsKey(key)) 
			value = values.get(key);
		boolean isEmpty = value == null || EMPTY.equals(value);
		boolean hasDefault =  deflt != null;
		if(isEmpty && hasDefault)
			return deflt;
		else return value;
	}

	public Map<String,String> getValues() {		
		return values;
	}
}
