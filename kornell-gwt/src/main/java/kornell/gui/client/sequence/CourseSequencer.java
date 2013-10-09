package kornell.gui.client.sequence;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.shared.data.Actom;
import kornell.core.shared.data.Contents;
import kornell.core.shared.data.ContentsCategory;
import kornell.core.shared.to.CourseTO;
import kornell.core.shared.util.StringUtils;
import kornell.gui.client.event.NavigationForecastEvent;
import kornell.gui.client.event.NavigationForecastEvent.Forecast;
import static kornell.gui.client.event.NavigationForecastEvent.Forecast.*;
import kornell.gui.client.presentation.course.CoursePlace;
import kornell.gui.client.widget.ExternalPageView;

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

	private CoursePlace place;
	private CourseTO courseTO;
	private Contents contents;
	private String currentKey;
	private String baseURL;
	private String courseUUID;

	private EventBus bus;

	public CourseSequencer(EventBus bus, KornellClient client) {
		GWT.log("new CourseSequencer");
		this.bus = bus;
		this.client = client;
		bus.addHandler(NavigationRequest.TYPE, this);
	}



	
	private List<Actom> actoms;

	private int currentIndex;

	private ExternalPageView externalPageView;



	private String nextKey() {
		String nextKey = isAtEnd() ? currentKey : getActoms().get(++currentIndex).getKey();
		return nextKey;
	}

	private List<Actom> getActoms() {
		if (actoms == null)
			actoms = ContentsCategory.collectActoms(contents);
		return actoms;
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
		return getActoms().get(currentIndex > 0 ? --currentIndex : 0).getKey();
	}

	private int getCurrentIndex() {
		return currentIndex;
	}

	@Override
	public void displayOn(FlowPanel contentPanel) {
		contentPanel.clear();
		externalPageView = new ExternalPageView(client);
		contentPanel.add(externalPageView);
		render(place);
	}

	private void walk() {		
		String src = StringUtils.composeURL(baseURL, currentKey);		
		GWT.log("Navigating to [" + src + "]");
		externalPageView.setSrc(src);
		evaluateNavigation();
	}

	private void evaluateNavigation() {
		Forecast f = isAtEnd() ? NEXT_NOT_OK : NEXT_OK;
		bus.fireEvent(new NavigationForecastEvent(f));
	}

	private boolean isAtEnd() {
		int index = getCurrentIndex();
		int end = getActoms().size() - 1;
		boolean isAtEnd = index >= end;
		return isAtEnd;
	}

	private void render(final CoursePlace place) {
		GWT.log("Rendering [" + place + "]");
		if (place == null)
			throw new IllegalArgumentException("Cannot render null place");
		courseUUID = place.getCourseUUID();
		fetchContentsAndGo();

	}

	private void fetchContentsAndGo() {
		client.course(courseUUID).contents(new Callback<Contents>(){
			@Override
			protected void ok(Contents contents) {
				setContents(contents);
				orientateAndGo();
			}
		});
	}

	private void orientateAndGo() {
		if (!orientateByQueryString())
			orientateToLastVisited();
		go();
	}

	private boolean orientateByQueryString() {
		String key = Window.Location.getParameter("key");
		if (key != null && !key.isEmpty()) {
			currentKey = key;
			return true;
		} else
			return false;
	}

	private void orientateToLastVisited() {
		String checkpoint = courseTO.getEnrollment().getLastActomVisited();
		if (checkpoint == null || checkpoint.isEmpty())
			currentKey = getActoms().get(0).getKey();
		else
			currentKey = checkpoint;
	}

	@Override
	public Sequencer withPlace(CoursePlace place) {
		this.place = place;
		return this;
	}
	
	private void setContents(Contents contents) {
		this.contents = contents;
		setCourseTO(contents.getCourseTO());
	}

	private void setCourseTO(CourseTO courseTO) {
		this.courseTO = courseTO;
		setBaseURL(courseTO.getBaseURL());
		
	}

	private void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}


}