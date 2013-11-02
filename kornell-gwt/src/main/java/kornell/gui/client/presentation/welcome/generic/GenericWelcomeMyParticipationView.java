package kornell.gui.client.presentation.welcome.generic;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.to.CoursesTO;
import kornell.gui.client.KornellConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;


public class GenericWelcomeMyParticipationView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericWelcomeMyParticipationView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	
	private KornellClient client;
	private PlaceController placeCtrl;
	private final EventBus eventBus = new SimpleEventBus();
	private KornellConstants constants = GWT.create(KornellConstants.class);
	
	
	public GenericWelcomeMyParticipationView(KornellClient client, PlaceController placeCtrl) {
		this.client = client;
		this.placeCtrl = placeCtrl;
		initWidget(uiBinder.createAndBindUi(this));
		initData();		
	}
	
	private void initData() {
		client.getCourses(new Callback<CoursesTO>() {
			@Override
			protected void ok(CoursesTO to) {
				display();
			}
		});
	}


	private void display() {
	}


}