package kornell.gui.client.presentation.welcome.generic;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.shared.data.CourseTO;
import kornell.core.shared.data.CoursesTO;
import kornell.gui.client.presentation.welcome.WelcomeView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class GenericWelcomeView extends Composite implements WelcomeView {
	interface MyUiBinder extends UiBinder<Widget, GenericWelcomeView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	FlowPanel pnlCourses;

	private KornellClient client;

	private PlaceController placeCtrl;

	public GenericWelcomeView(KornellClient client, PlaceController placeCtrl) {
		this.client = client;
		this.placeCtrl = placeCtrl;
		initWidget(uiBinder.createAndBindUi(this));
		initData();
	}

	private void initData() {
		client.getCourses(new Callback<CoursesTO>() {
			@Override
			protected void ok(CoursesTO to) {
				display(to);
			}
		});
	}

	private void display(CoursesTO to) {
		for (final CourseTO course : to.getCourses()) {
			GenericCourseSummaryView courseSummaryView 
				= new GenericCourseSummaryView(placeCtrl,course);
			pnlCourses.add(courseSummaryView);
		}
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}

}
