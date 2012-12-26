package kornell.client.view.generic;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.api.client.data.Person;
import kornell.client.presenter.home.HomeView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class GenericHomeView  extends Composite implements HomeView {
	interface MyUiBinder extends UiBinder<Widget, GenericHomeView> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private KornellClient client;
	
	//private PlaceController placeCtrl;
	@UiField Label lblWelcome;
	 
	public GenericHomeView(PlaceController placeCtrl,KornellClient client) {
		//this.placeCtrl = placeCtrl;
		this.client = client;
		
		client.getCurrentUser(new Callback(){
			@Override
			protected void ok(Person person) {
				lblWelcome.setText("Welcome "+person.getFullName());
			}
		});
		GWT.log("Oooops, should not happen");
	    initWidget(uiBinder.createAndBindUi(this));
	}
	

	@Override
	public void setPresenter(Presenter presenter) {
	 		
	}
	

}
