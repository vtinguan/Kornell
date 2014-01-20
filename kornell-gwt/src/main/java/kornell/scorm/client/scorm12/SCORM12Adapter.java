package kornell.scorm.client.scorm12;

import com.google.gwt.core.shared.GWT;

public class SCORM12Adapter {
	private static final String TRUE = "true";
	private static final String FALSE = "false";

	private String lastError = "0";
	private CMIDataModel cmi;
	
	public SCORM12Adapter(CMIDataModel cmi) {
		this.cmi = cmi;
	}
	
	public String LMSInitialize(String param){
		GWT.log("LMSInitialize["+param+"]");
		return TRUE;
	}
	
	public String LMSFinish(String param){
		GWT.log("LMSFinish["+param+"]");
		return TRUE;
	}
	
	public String LMSGetLastError(){
		GWT.log("LMSGetLastError[]");
		return lastError;
	}
	
	public String LMSGetValue(String param){
		String result = null;
		if(isCMIElement(param))
			result = cmi.getValue(param);
		GWT.log("LMSGetValue["+param+"] = "+result);
		return result;
	}

	private boolean isCMIElement(String param) {
		return param != null && param.startsWith("cmi");
	}
	
	public String LMSSetValue(String param, String value){
		String result = FALSE;
		if(isCMIElement(param))
			return cmi.setValue(param,value);
		GWT.log("LMSSetValue["+param+","+value+"] = "+result);
		return result;
	}
	
	
}
