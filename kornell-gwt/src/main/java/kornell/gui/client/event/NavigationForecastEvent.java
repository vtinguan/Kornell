package kornell.gui.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class NavigationForecastEvent extends GwtEvent<NavigationForecastEventHandler> {
	public static Type<NavigationForecastEventHandler> TYPE = new Type<NavigationForecastEventHandler>();
	
	public enum Forecast {
		NEXT_OK,
		NEXT_NOT_OK;
		
		public NavigationForecastEvent get(){
			return new NavigationForecastEvent(this);
		}
	} 
	
	public NavigationForecastEvent(Forecast forecast){
		this.forecast = forecast;
	}
	
	Forecast forecast;
	
	@Override
	public Type<NavigationForecastEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(NavigationForecastEventHandler handler) {
		switch(forecast){
			case NEXT_NOT_OK: handler.onNextActivityNotOK(this);
							  break;
			case NEXT_OK: handler.onNextActivityOK(this);
						  break;
		}
				
	}

}
