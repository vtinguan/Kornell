package kornell.api.client.data;

import com.google.gwt.core.client.JavaScriptObject;

public class Person extends JavaScriptObject {
	public static final String MIME_TYPE = "application/vnd.kornell.v1.person+json";

	protected Person() {
	}

	public final native String getFullName() /*-{
		return this.fullName;
	}-*/;

	public static native Person parseJson(String jsonText) /*-{
		return eval(jsonText);
	}-*/;
}
