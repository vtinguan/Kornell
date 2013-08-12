package kornell.gui.client.content;

import static com.google.gwt.http.client.RequestBuilder.HEAD;
import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.shared.to.CourseTO;
import kornell.gui.client.presentation.atividade.AtividadePlace;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.web.bindery.event.shared.EventBus;

public class NamingConventionSequencer implements Sequencer {

	private KornellClient client;
	private IFrameElement iframe;
	private String assetsURL;
	private PlaceController ctrl;
	private AtividadePlace place;

	public NamingConventionSequencer(EventBus bus,
			PlaceController ctrl,
			KornellClient client) {
		bus.addHandler(NavigationRequest.TYPE, this);
		this.client = client;
		this.ctrl = ctrl;
		iframe = Document.get().createIFrameElement();
		iframe.addClassName("externalContent");		
	}

	public Sequencer at(AtividadePlace place) {
		this.place = place;
		return this;
	}

	@Override
	public void displayOn(FlowPanel contentPanel) {
		contentPanel.clear();		
		contentPanel.getElement().appendChild(iframe);
		render();
	}

	private void render() {
		if (place == null) {
			GWT.log("null place???");
			return;
		}
		Integer position = place.getPosition();
		//TODO: Refator to future/promise (client.withCourse(uuid).do(showCurrentPosition());)
		final String file = fileOf(position);
		if (assetsURL != null)
			go(assetsURL+file);
		else {
			String uuid = place.getCourseUUID();
			client.getCourseTO(uuid,new Callback<CourseTO>(){
				@Override
				protected void ok(CourseTO to) {
					assetsURL = to.getCourse().getAssetsURL();
					go(assetsURL+file);
				}			
			});
		}
	}


	private void go(final String location) {
		try {
			new RequestBuilder(HEAD, location).sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {					
					if (response.getStatusCode() == 200){
						iframe.setSrc(location);
					}else if(response.getStatusCode() == 0){
						//TODO: Add application warning
						GWT.log("XHR cancelled, probably reject by same origin policy. Please configure cross origin resource sharing.");
						iframe.setSrc(location);
					}
					else GWT.log("Error displaying content (onResponseReceived)");					
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					GWT.log("Error displaying content (onError)",exception);
				}
			});
		} catch (RequestException e) {
			GWT.log("Error displaying content (catch)",e);
		}
		
		//TODO: Preload next page
	}
	
	@Override
	public void onContinue(NavigationRequest event) {
		ctrl.goTo(place.next());
	}

	@Override
	public void onPrevious(NavigationRequest event) {		
		ctrl.goTo(place.previous());
	}
	
	
	private static String fileOf(Integer position) {
		if(position <= 0) return "home.html";
		//TODO: use GWT formatter (can not access api now, gimme a break)		
		return position+".html";
	}

}
