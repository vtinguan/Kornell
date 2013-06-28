package kornell.gui.client.presentation.course.generic;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.api.client.data.Person;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.atividade.AtividadePlace;
import kornell.gui.client.presentation.course.CoursePlace;
import kornell.gui.client.presentation.course.CourseView;
import kornell.gui.client.presentation.welcome.generic.GenericMenuLeftView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;


public class GenericCourseCourseView extends Composite implements CourseView {
	interface MyUiBinder extends UiBinder<Widget, GenericCourseCourseView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Button btnAgree;
	
	private KornellClient client;

	private PlaceController placeCtrl;

	private Presenter presenter;

	private KornellConstants constants = GWT.create(KornellConstants.class);
	
	private GenericMenuLeftView menuLeftView;
	
	
	public GenericCourseCourseView(KornellClient client, PlaceController placeCtrl) {
		this.client = client;
		this.placeCtrl = placeCtrl;
		initWidget(uiBinder.createAndBindUi(this));
		initData();		
		System.out.println("courseview");
	}
	
	private void initData() {
		client.getCurrentUser(new Callback<Person>() {
			@Override
			protected void ok(Person person) {
				display(person);
			}
		});
	}


	private void display(Person person) {
	}

	@UiHandler("btnAgree")
	void handleClickAll(ClickEvent e) {
		placeCtrl.goTo(new AtividadePlace(((CoursePlace) placeCtrl.getWhere()).getCourseUUID(), 0));		
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

}