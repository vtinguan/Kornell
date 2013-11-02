package kornell.gui.client.event;

import kornell.core.to.UserInfoTO;

import com.google.gwt.event.shared.EventHandler;

public interface LoginEventHandler extends EventHandler {
	
	public void onLogin(UserInfoTO user);

}
