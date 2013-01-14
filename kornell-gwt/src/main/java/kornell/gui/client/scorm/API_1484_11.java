package kornell.gui.client.scorm;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;

public final class API_1484_11 {

	protected API_1484_11() {
	}
	
	public void Initialize(){
		Window.alert("INITIALIZE");

		GWT.log("** INITIALIZE CALLED **");
	}
	
	public String teste(){
		Window.alert("teste");
		return "teste uala";
	}
	
	public static API_1484_11 create(){
		return new API_1484_11();
	}

}
