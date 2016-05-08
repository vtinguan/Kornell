package kornell.gui.client.util;

import kornell.scorm.client.scorm12.SCORM12Adapter;

public class Console {
	public static native void log(String msg) /*-{
		console.log(msg);
	}-*/;
}
