package kornell.gui.client.presentation.course;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.lom.Contents;
import kornell.gui.client.presentation.course.details.generic.GenericCourseDetailsView;
import kornell.gui.client.presentation.util.LoadingPopup;
import kornell.gui.client.sequence.SequencerFactory;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class ClassroomPresenter implements ClassroomView.Presenter {
	private ClassroomView view;
	private ClassroomPlace place;
	private SequencerFactory sequencer;
	private KornellClient client;
	private EventBus bus;
	private Contents contents;

	public ClassroomPresenter(ClassroomView view,
			PlaceController placeCtrl,
			SequencerFactory seqFactory, KornellClient client, EventBus bus) {
		this.view = view;
		view.setPresenter(this);
		this.sequencer = seqFactory;
		this.client = client;
	}

	private void displayPlace() {
		LoadingPopup.show();
		client.enrollment(place.getEnrollmentUUID()).contents(new Callback<Contents>() {
			@Override
			public void ok(Contents contents) {
				setContents(contents);
				view.display();
				LoadingPopup.hide();
				sequencer.withPlace(place)
						.withPanel(getPanel())
						.go(contents);
			}

		});
	}

	private FlowPanel getPanel() {
		return view.getContentPanel();
	}
	
	@Override
	public Contents getContents(){
		return contents;
	}
	
	private void setContents(Contents contents){
		this.contents = contents;
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	public void setPlace(ClassroomPlace place) {
		this.place = place;
		displayPlace();
	}
}
