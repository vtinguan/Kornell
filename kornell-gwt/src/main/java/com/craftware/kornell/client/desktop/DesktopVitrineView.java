package com.craftware.kornell.client.desktop;

import com.craftware.kornell.client.presenter.home.HomePlace;
import com.craftware.kornell.client.presenter.vitrine.VitrineView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
//HTTP

public class DesktopVitrineView  extends Composite implements VitrineView {
	interface MyUiBinder extends UiBinder<Widget, DesktopVitrineView> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	private PlaceController placeCtrl;

	 
	public DesktopVitrineView(PlaceController placeCtrl) {
		this.placeCtrl = placeCtrl;
	    initWidget(uiBinder.createAndBindUi(this));
	}
	

	@Override
	public void setPresenter(Presenter presenter) {
	 		
	}
	
	@UiHandler("btnDoTheLocomotion")
	void doTheLocomotion(ClickEvent e) {
		// Send request to server and catch any errors.
		//TODO: Make URLs configurable, conform to same origin policy
	    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "http://api.kornell.craftware.com:8080/users/ftal");

	    try {
	      builder.sendRequest(null, new RequestCallback() {
	        public void onError(Request request, Throwable exception) {
	          displayError("Couldn't retrieve JSON");
	        }

	        public void onResponseReceived(Request request, Response response) {
	          if (200 == response.getStatusCode()) {
	        	  Window.alert("All right, u got it");
	        	  Window.alert(response.getText());
	          } else {
	            displayError("Couldn't retrieve JSON (" + response.getStatusText()
	                + ")");
	          }
	        }
	      });
	    } catch (RequestException ex) {
	      displayError("Couldn't retrieve JSON");
	    }	   

	}
	
	@UiHandler("btnLogin")
	void doLogin(ClickEvent e) {		
	    placeCtrl.goTo(new HomePlace());
	}


	private void displayError(String msg) {
		// TODO Auto-generated method stub
		Window.alert(msg);
	}

}
