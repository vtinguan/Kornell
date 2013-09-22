package kornell.gui.client.sequence;

import static com.google.gwt.http.client.RequestBuilder.HEAD;
import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.shared.to.CourseTO;
import kornell.gui.client.event.NavigationForecastEvent;
import kornell.gui.client.presentation.course.CoursePlace;

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

@Deprecated
public class NamingConventionSequencer implements Sequencer {

	private KornellClient client;
	private IFrameElement iframe;
	private String assetsURL;
	private PlaceController ctrl;
	private CoursePlace place;
	private EventBus bus;

	public NamingConventionSequencer(EventBus bus,
			PlaceController ctrl,
			KornellClient client) {
		bus.addHandler(NavigationRequest.TYPE, this);
		this.client = client;
		this.ctrl = ctrl;
		this.bus = bus;
		iframe = Document.get().createIFrameElement();
		iframe.addClassName("externalContent");
		throw new IllegalStateException("NamingConventionSequencer is not usable anymore, it just exists as a reference.");
	}

	public Sequencer at(CoursePlace place) {
		this.place = place;
		return this;
	}

	@Override
	public void displayOn(FlowPanel contentPanel) {
		contentPanel.clear();		
		contentPanel.getElement().appendChild(iframe);
		render(place);
	}
	
	interface AssetsURLCallback {
		void withAssetsURL(String assetsURL);
	} 
	
	private void withAssetsUrl(final AssetsURLCallback cb){
		if (assetsURL != null)
			cb.withAssetsURL(assetsURL);			
		else {
			String uuid = place.getCourseUUID();
			client.getCourseTO(uuid,new Callback<CourseTO>(){
				@Override
				protected void ok(CourseTO to) {
					assetsURL = to.getCourse().getAssetsURL();
					cb.withAssetsURL(assetsURL);
				}			
			});
		}
	}

	private void render(final CoursePlace place) {
		if (place == null) {
			GWT.log("null place???");
			return;
		}		
		
		withAssetsUrl(new AssetsURLCallback(){
			@Override
			public void withAssetsURL(String assetsURL) {				
				String currentURL = assetsURL+fileOf(place);
				go(currentURL);
				
				String nextURL ="";//assetsURL+fileOf(place.next()); 
				warnIfNextNotOK(nextURL);
			}
		});
	}
	
	

	private void warnIfNextNotOK(String nextLocation) {
		try {
			new RequestBuilder(HEAD, nextLocation).sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {					
					boolean nextOK = response.getStatusCode() == 200;
					if (nextOK){
						bus.fireEvent(NavigationForecastEvent.Forecast.NEXT_OK.get());
					}else {
						bus.fireEvent(NavigationForecastEvent.Forecast.NEXT_NOT_OK.get());
					}
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					GWT.log("Error displaying content (onError)",exception);
				}
			});
		} catch (RequestException e) {
			GWT.log("Error displaying content (catch)",e);
		}
		
	}


	private void go(final String location) {
		iframe.setSrc(location);
		/*
		try {
			new RequestBuilder(HEAD, location).sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {					
					boolean locationExists = response.getStatusCode() == 200;
					if (locationExists){
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
		*/
		
	}
	
	@Override
	public void onContinue(NavigationRequest event) {
		//ctrl.goTo(place.next());
	}

	@Override
	public void onPrevious(NavigationRequest event) {		
		//ctrl.goTo(place.previous());
	}
	
	
	private static String fileOf(CoursePlace place) {
		//Integer position = place.getPosition();
		//if(position <= 0) return "home.html";
		//TODO: use GWT formatter (can not access api now, gimme a break)		
		return "";// position+".html";
	}

}
