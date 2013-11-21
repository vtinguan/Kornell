package kornell.gui.client.presentation.course.course.generic;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.api.client.UserSession;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.course.CoursePlace;
import kornell.gui.client.presentation.course.course.CourseHomePlace;
import kornell.gui.client.presentation.course.course.CourseHomeView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;


public class GenericCourseHomeView extends Composite implements CourseHomeView {
	interface MyUiBinder extends UiBinder<Widget, GenericCourseHomeView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Button btnAgree;
	
	private UserSession session;
	private PlaceController placeCtrl;
	private EventBus bus;
	private Presenter presenter;
	private KornellConstants constants = GWT.create(KornellConstants.class);
	
	
	public GenericCourseHomeView(EventBus eventBus, KornellClient client, PlaceController placeCtrl) {
		this.bus = eventBus;
		this.session = session;
		this.placeCtrl = placeCtrl;
		initWidget(uiBinder.createAndBindUi(this));
		initData();		
	}
	
	private void initData() {
		session.getCurrentUser(new Callback<UserInfoTO>() {
			@Override
			public void ok(UserInfoTO user) {
				display(user);
			}
		});
	}


	private void display(UserInfoTO person) {
	}

	@UiHandler("btnAgree")
	void handleClickAll(ClickEvent e) {
		placeCtrl.goTo(new CoursePlace(((CourseHomePlace) placeCtrl.getWhere()).getCourseUUID()));		
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

}