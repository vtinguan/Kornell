package kornell.gui.client.sequence;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.shared.to.CourseTO;
import kornell.gui.client.presentation.course.CoursePlace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.web.bindery.event.shared.EventBus;

public class CourseSequencer implements Sequencer {
	private KornellClient client;

	private IFrameElement iframe;
	private CoursePlace place;
	private CourseTO courseTO;

	public CourseSequencer(EventBus bus, KornellClient client) {
		bus.addHandler(NavigationRequest.TYPE, this);
		iframe = Document.get().createIFrameElement();
		iframe.addClassName("externalContent");
		this.client = client;
	}

	@Override
	public void onContinue(NavigationRequest event) {
		GWT.log("CONTINUE");
	}

	@Override
	public void onPrevious(NavigationRequest event) {
		GWT.log("PREVIOUS");
	}

	@Override
	public void displayOn(FlowPanel contentPanel) {
		contentPanel.clear();
		contentPanel.getElement().appendChild(iframe);
		render(place);
	}

	private void render() {
		String baseURL = courseTO.getBaseURL();
		String path = courseTO.getActoms().get(0);
		iframe.setSrc(baseURL+path);
	}

	private void render(final CoursePlace place) {
		if (place == null)
			throw new IllegalArgumentException("Cannot render null place");
		String uuid = place.getCourseUUID();
		client.getCourseTO(uuid, new Callback<CourseTO>() {
			@Override
			protected void ok(CourseTO to) {
				courseTO = to;
				render();
			}
		});
		/*
		 * withAssetsUrl(new AssetsURLCallback() {
		 * 
		 * @Override public void withAssetsURL(String assetsURL) { String
		 * currentURL = assetsURL + fileOf(place); go(currentURL);
		 * 
		 * String nextURL = assetsURL + fileOf(place.next());
		 * warnIfNextNotOK(nextURL); } });
		 */
	}

	@Override
	public Sequencer at(CoursePlace place) {
		this.place = place;
		return this;
	}

}
