package kornell.scorm.client.scorm12;

public class SCORM12Binder {
	public static native void bind(SCORM12Adapter api) /*-{
		console.debug("Binding SCORM 1.2 Adapter to window");
		var API = $wnd.API || {};
		
		//[instance-expr.]@class-name::method-name(param-signature)(arguments)
		API.LMSInitialize = function(param) {
			return api.@kornell.scorm.client.scorm12.SCORM12Adapter::LMSInitialize(Ljava/lang/String;)(param);			
		}
		
		API.LMSFinish = function(param) {
			return api.@kornell.scorm.client.scorm12.SCORM12Adapter::LMSFinish(Ljava/lang/String;)(param);			
		}
		
		$wnd.API = API;
		console.debug("Scorm 1.2 Adapter bound");
	}-*/;
}
