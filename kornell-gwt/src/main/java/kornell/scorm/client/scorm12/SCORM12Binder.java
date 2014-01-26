package kornell.scorm.client.scorm12;

public class SCORM12Binder {
	public static native void bind(SCORM12Adapter api) /*-{
		
		
		//if (typeof console == "undefined" || typeof console.log == "undefined" || typeof console.debug == "undefined"){
		//   var console = { log: function(){}, debug: function(){} }; 
		//}
		
		var API = $wnd.API || {};
		
		//[instance-expr.]@class-name::method-name(param-signature)(arguments)
		API.LMSInitialize = function(param) {
			return api.@kornell.scorm.client.scorm12.SCORM12Adapter::LMSInitialize(Ljava/lang/String;)(param);			
		}
		
		API.LMSFinish = function(param) {
			return api.@kornell.scorm.client.scorm12.SCORM12Adapter::LMSFinish(Ljava/lang/String;)(param);			
		}
		
		API.LMSGetLastError = function(){
			return api.@kornell.scorm.client.scorm12.SCORM12Adapter::LMSGetLastError()();
		}
		
		API.LMSGetValue = function(param) {
			return api.@kornell.scorm.client.scorm12.SCORM12Adapter::LMSGetValue(Ljava/lang/String;)(param);			
		} 
		
		API.LMSSetValue = function(param,value) {
			return api.@kornell.scorm.client.scorm12.SCORM12Adapter::LMSSetValue(Ljava/lang/String;Ljava/lang/String;)(param,value);			
		} 
		
		$wnd.API = API;
	}-*/;
}
