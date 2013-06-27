package kornell.gui.client.presentation.terms.generic;

import java.math.BigDecimal;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.api.client.data.Person;
import kornell.core.shared.data.CourseTO;
import kornell.core.shared.data.CoursesTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.bar.ActivityBarView;
import kornell.gui.client.presentation.bar.MenuBarView;
import kornell.gui.client.presentation.bar.generic.GenericActivityBarView;
import kornell.gui.client.presentation.terms.TermsPlace;
import kornell.gui.client.presentation.terms.TermsView;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.presentation.welcome.WelcomePlace;
import kornell.gui.client.presentation.welcome.generic.GenericCourseSummaryView;
import kornell.gui.client.presentation.welcome.generic.GenericMenuLeftView;

import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;


public class GenericTermsView extends Composite implements TermsView {
	interface MyUiBinder extends UiBinder<Widget, GenericTermsView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);


	@UiField
	Paragraph titleUser;
	@UiField
	Button btnAgree;
	@UiField
	Button btnDontAgree;
	

	private static String COURSES_ALL = "all";
	private static String COURSES_IN_PROGRESS = "inProgress";
	private static String COURSES_TO_START = "toStart";
	private static String COURSES_TO_ACQUIRE = "toAcquire";
	private static String COURSES_FINISHED = "finished";
	
	private KornellClient client;

	private PlaceController placeCtrl;
	
	private String displayCourses;

	private KornellConstants constants = GWT.create(KornellConstants.class);
	
	private GenericMenuLeftView menuLeftView;
	
	
	public GenericTermsView(KornellClient client, PlaceController placeCtrl) {
		this.client = client;
		this.placeCtrl = placeCtrl;
		initWidget(uiBinder.createAndBindUi(this));
		initData();		
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
		titleUser.setText(person.getFullName());
		//TODO i18n
		btnAgree.setText("Concordo".toUpperCase());
		btnDontAgree.setText("Não Concordo".toUpperCase());
		
	}

	@UiHandler("btnAgree")
	void handleClickAll(ClickEvent e) {
		placeCtrl.goTo(new WelcomePlace());
	}

	@UiHandler("btnDontAgree")
	void handleClickInProgress(ClickEvent e) {
		placeCtrl.goTo(new VitrinePlace());
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}

}