package kornell.scorm.client.scorm12;

import java.util.Map;

import kornell.api.client.Callback;
import kornell.core.entity.ActomEntries;

import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.event.shared.EventBus;

public class CMIDataModel extends CMICoreModel {
	boolean dirty;
	private SCORM12Adapter api;
	private EventBus bus;

	public CMIDataModel(SCORM12Adapter api, EventBus bus,
			Map<String, String> entries) {
		this.api = api;
		this.entries = entries;
		this.bus = bus;
	}

	public String getValue(String elementKey) {
		if (elementKey == null) {
			GWT.log("!!! Null Element !!!");
			throw new NullPointerException(
					"Data model element can not be null.");
		}
		CMIDataElement element = elements.get(elementKey);
		if (element != null)
			return getElementValue(element);
		// TODO: Set Appropriate Error
		throw new IllegalArgumentException("Unkown CMI Data Model Element ["
				+ elementKey + "]");
	}

	private String getElementValue(CMIDataElement element) {
		String key = element.getKey();
		String value = EMPTY;
		if (entries.containsKey(key))
			value = entries.get(key);
		boolean isEmpty = isEmpty(value);
		String defaultValue = element.getDefaultValue();
		boolean hasDefault = !isEmpty(defaultValue);
		if (isEmpty && hasDefault)
			return defaultValue;
		return value;
	}

	public String setValue(String elementKey, String value) {
		CMIDataElement element = elements.get(elementKey);
		if (element != null) {
			setElementValue(element, value);
			return TRUE;
		} else {
			// TODO: SetLastError, etc
			return FALSE;
		}
	}

	private void setElementValue(final CMIDataElement element, final String value) {
		entries.put(element.getKey(), value);
		final Action onSet = element.getOnSet();
		api.onDirtyData(new Callback<ActomEntries>() {
			@Override
			public void ok(ActomEntries to) {
				if (onSet != null){
					GWT.log("Executing onSet action for ["+element.getKey()+"]");
					onSet.execute(bus);
				}
			}
		});
	}

	private boolean isEmpty(String value) {
		return value == null || EMPTY.equals(value);
	}

	public Map<String, String> getValues() {
		return entries;
	}
}
