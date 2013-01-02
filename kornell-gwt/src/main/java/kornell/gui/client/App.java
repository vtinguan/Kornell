package kornell.gui.client;

import kornell.gui.client.presentation.vitrine.VitrinePlace;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;

public class App {
	private final EventBus eventBus;
	private final PlaceController placeController;
	private final ActivityManager activityManager;	
	private final PlaceHistoryHandler historyHandler;

	public App(EventBus eventBus,
			PlaceController placeController,
		    ActivityManager activityManager, 			
			PlaceHistoryHandler historyHandler) {
		this.eventBus = eventBus;
	    this.placeController = placeController;
	    this.activityManager = activityManager;
		this.historyHandler = historyHandler;
	}
	


	public void run(HasWidgets.ForIsWidget parentView) {
		eventBus.addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
			@Override
			public void onPlaceChange(PlaceChangeEvent event) {
				GWT.log("Plance Changed to "+event.getNewPlace());	
			}
		});
		
		SimplePanel shell = new SimplePanel();
		shell.addStyleName("wrapper");
		parentView.add(shell);
	    activityManager.setDisplay(shell);
	    historyHandler.register(placeController, eventBus, new VitrinePlace());		
		historyHandler.handleCurrentHistory();
		
	}
}
