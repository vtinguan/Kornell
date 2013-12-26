package kornell.scorm.client;

import java.util.HashMap;
import java.util.Map;

import kornell.gui.client.sequence.NavigationRequest;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.web.bindery.event.shared.EventBus;

//TODO: Reconsider using gwt-exporter or $entry when #4941 is fixed ( http://code.google.com/p/google-web-toolkit/issues/detail?id=4941 )
//TODO: Use Logger instead of GWT.log
//TODO: Consider caching navigation with "future-able" api 
public final class API_1484_11 {
	private Map<String,Object> values = new HashMap<String,Object>();
	private EventBus eventBus;

	public API_1484_11(EventBus eventBus) {
		this.eventBus = eventBus;
		values.put("adl.nav.request_valid.continue", "true");
		values.put("adl.nav.request_valid.previous", "true");
	
	}

	public void Initialize() {}
	
	public void Terminate(){}

	public String GetLastError() {
		return "0";
	}

	public String GetValue(String key) {
		GWT.log("SCO request for [" + key + "]");
		return values.containsKey(key) ?
			   values.get(key).toString()
			   : "unknown";
	}
	
	public String setValue(String key, Object value){
		GWT.log("SCO setting [" + key + "] to ["+value+"]");		
		fireEvent(key,value);
		values.put(key, value);
		return "true";
	}
	
	private void fireEvent(String key, Object value) {
		 if(key.equals("adl.nav.request")){
		 	eventBus.fireEvent(NavigationRequest.valueOf(value.toString()));
		 }
	}


	public String SetNumber(String key, double value){
		return setValue(key,value); 
	}
	
	public String SetString(String key, String value){
		return setValue(key,value);
	}

	public void bindToWindow(){
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				API_1484_11.bindToWindow(API_1484_11.this);
			}
		});
	}
	
	public static native void bindToWindow(API_1484_11 api) /*-{
		$wnd.API_1484_11 = {
			Initialize : function() {
				$entry(api.@kornell.scorm.client.API_1484_11::Initialize()());
			},
			Terminate : function() {
				$entry(api.@kornell.scorm.client.API_1484_11::Terminate()());
			},
			GetLastError : function() {
				return api.@kornell.scorm.client.API_1484_11::GetLastError()();				
			},
			GetValue : function(key) {
				return api.@kornell.scorm.client.API_1484_11::GetValue(Ljava/lang/String;)(key);
			},
			SetValue : function(key,value){
				if (typeof value == "number")
				return api.@kornell.scorm.client.API_1484_11::SetNumber(Ljava/lang/String;D)(key,value);
				else if(typeof value == "string")
				return api.@kornell.scorm.client.API_1484_11::SetString(Ljava/lang/String;Ljava/lang/String;)(key,value);
				else throw "cannot set value of type " + typeof value
			}
			
		};
	}-*/;

}
