package kornell.gui.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class HideSouthBarEvent extends GwtEvent<HideSouthBarEventHandler>{

	public static final Type<HideSouthBarEventHandler> TYPE = new Type<HideSouthBarEventHandler>();
	
	private boolean hideSouthBar;
	private boolean testPlace;
	
	public HideSouthBarEvent(boolean hideSouthBar){
		this.hideSouthBar = hideSouthBar;
	}
	
	public HideSouthBarEvent(){
		this.testPlace = true;
	}
	
	@Override
	public Type<HideSouthBarEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(HideSouthBarEventHandler handler) {
		handler.onHideSouthBar(this);		
	}

	public boolean isHideSouthBar() {
		return hideSouthBar;
	}

	public void setHideSouthBar(boolean hideSouthBar) {
		this.hideSouthBar = hideSouthBar;
	}

	public boolean isTestPlace() {
		return testPlace;
	}

	public void setTestPlace(boolean testPlace) {
		this.testPlace = testPlace;
	}
}