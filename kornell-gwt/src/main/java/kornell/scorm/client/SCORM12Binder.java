package kornell.scorm.client;

public class SCORM12Binder {
	public static native void bind(SCORM2004Adapter api) /*-{
		
		if (typeof console == "undefined" || typeof console.log == "undefined" || typeof console.debug == "undefined"){
		   var console = { log: function(){}, debug: function(){} }; 
		}
			
		console.debug("Binding SCORM 1.2 Adapter to window");
		var API = $wnd.API || {};
		
		//[instance-expr.]@class-name::method-name(param-signature)(arguments)
		API.LMSInitialize = function(param) {
			return api.@kornell.scorm.client.SCORM12Adapter::LMSInitialize(Ljava/lang/String;)(param);			
		}
		
		API.LMSFinish = function(param) {
			return api.@kornell.scorm.client.SCORM12Adapter::LMSFinish(Ljava/lang/String;)(param);			
		}
		
		$wnd.API = API;
		console.debug("Scorm 1.2 Adapter bound");
	}-*/;
}
