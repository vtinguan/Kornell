package kornell.gui.client.util;


public class Debugging {
	
	public static native void bindToWnd(Debugger api) /*-{
		var KDG = {};

		KDG.ping = function() {
			api.@kornell.gui.client.util.Debugger::ping()();
		}

		$wnd.kdg = KDG;
	}-*/;

	public static void init() {
		Debugging.bindToWnd(new Debugger());
	}
}
