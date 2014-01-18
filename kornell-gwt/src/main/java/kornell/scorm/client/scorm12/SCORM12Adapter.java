package kornell.scorm.client.scorm12;

import com.google.gwt.core.shared.GWT;

public class SCORM12Adapter {
	
	public String LMSInitialize(String param){
		GWT.log("LMSInitialize["+param+"]");
		return "true";
	}
	
	public String LMSFinish(String param){
		GWT.log("LMSFinish["+param+"]");
		return "true";
	}
	
	
}
