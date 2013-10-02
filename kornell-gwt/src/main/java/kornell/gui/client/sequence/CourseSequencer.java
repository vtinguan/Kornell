package kornell.gui.client.sequence;

import java.util.Collections;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.shared.to.CourseTO;
import kornell.gui.client.event.NavigationForecastEvent;
import kornell.gui.client.event.NavigationForecastEvent.Forecast;
import static kornell.gui.client.event.NavigationForecastEvent.Forecast.*;
import kornell.gui.client.presentation.course.CoursePlace;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.web.bindery.event.shared.EventBus;

public class CourseSequencer implements Sequencer {
	private KornellClient client;

	private IFrameElement iframe;
	private CoursePlace place;
	private CourseTO courseTO;
	private String currentKey;
	private String baseURL;

	private EventBus bus;

	public CourseSequencer(EventBus bus, KornellClient client) {
		GWT.log("new CourseSequencer");
		this.bus = bus;
		this.client = client;
		bus.addHandler(NavigationRequest.TYPE, this);
		createIFrame();
	}

	private void createIFrame() {
		if(iframe == null){
			iframe = Document.get().createIFrameElement();
			iframe.addClassName("externalContent");
		}
		placeIframe();

		
		// Weird yet simple way of solving FF's weird behavior
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				Scheduler.get().scheduleDeferred(new Command() {					
					@Override
					public void execute() {
						placeIframe();						
					}
				});
			}
		});
		
	}

	// TODO: fetch these dynamically
	// TODO: Extract view
	int NORTH_BAR = 45;
	int SOUTH_BAR = 35;

	private void placeIframe() {
		iframe.setPropertyString("width", Window.getClientWidth() + "px");
		iframe.setPropertyString("height", (Window.getClientHeight()
				- SOUTH_BAR - NORTH_BAR)
				+ "px");
	}

	

	private String nextKey() {
		String nextKey = isAtEnd() ? 
				currentKey : 
				courseTO.getActoms().get(getCurrentIndex() + 1);
		return nextKey;
	}
	
	@Override
	public void onContinue(NavigationRequest event) {
		currentKey = nextKey();
		go();
	}

	private void go() {
		dropBreadcrumb();
		walk();
	}

	private void dropBreadcrumb() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPrevious(NavigationRequest event) {
		currentKey = prevKey();
		go();
	}

	private String prevKey() {
		int index = getCurrentIndex();
		return courseTO.getActoms().get(index > 0 ? index - 1 : 0);
	}

	private int getCurrentIndex() {
		return Collections.binarySearch(courseTO.getActoms(), currentKey);
	}

	@Override
	public void displayOn(FlowPanel contentPanel) {
		contentPanel.clear();
		contentPanel.getElement().appendChild(iframe);
		render(place);
	}

	private void walk() {
		String src = baseURL + currentKey;
		GWT.log("Navigating to ["+src+"]");
		iframe.setSrc(src);
		evaluateNavigation();
	}

	private void evaluateNavigation() {
		Forecast f = isAtEnd() ? NEXT_NOT_OK : NEXT_OK;
		bus.fireEvent(new NavigationForecastEvent(f));
	}

	private boolean isAtEnd() {
		int index = getCurrentIndex();
		int end = courseTO.getActoms().size()-1;
		boolean isAtEnd = index >= end;
		return isAtEnd;
	}

	private void render(final CoursePlace place) {
		GWT.log("Rendering ["+place+"]");		
		if (place == null)
			throw new IllegalArgumentException("Cannot render null place");
		String uuid = place.getCourseUUID();
		client.getCourseTO(uuid, new Callback<CourseTO>() {
			@Override
			protected void ok(CourseTO to) {
				courseTO = to;
				baseURL = to.getBaseURL(); 				
				String checkpoint = courseTO.getEnrollment().getLastActomVisited();
				currentKey = checkpoint.isEmpty() ? 
						courseTO.getActoms().get(0) : 
						checkpoint;
				go();
			}
		});		
	}

	@Override
	public Sequencer withPlace(CoursePlace place) {
		this.place = place;
		return this;
	}

}