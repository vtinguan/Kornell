package kornell.scorm.client.scorm12;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kornell.api.client.KornellClient;

import com.google.gwt.core.shared.GWT;

public class CMIDataModel {

	// Data Model Constants
	private static final String CHILDREN = "_children";
	private static final String LESSON_STATUS = "cmi.core.lesson_status";
	private static final String NOT_ATTEMPTED = "not attempted";
	private KornellClient client;
	
	public CMIDataModel(KornellClient client) {
		this.client = client;
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
		return null;
	}

	private String getLessonStatus() {
		String status = get(LESSON_STATUS);
		if (status == null)
			return NOT_ATTEMPTED;
		return null;
	}

	public String setValue(String param, String value) {
		return put(param, value);
	}
	
	public String get(String key) {
		return client.actom("???ActomKey???").get(key);
	}

	public String put(String key, String value) {
		return client.actom("???ActomKey???").put(key,value);
	}
}
