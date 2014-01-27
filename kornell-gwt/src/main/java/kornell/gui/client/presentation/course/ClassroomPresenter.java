package kornell.gui.client.presentation.course;

import kornell.gui.client.sequence.SequencerFactory;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class ClassroomPresenter implements ClassroomView.Presenter {
	private ClassroomView view;
	private ClassroomPlace place;
	private SequencerFactory sequencer;

	public ClassroomPresenter(ClassroomView view,
			PlaceController placeCtrl,
			SequencerFactory seqFactory) {
		this.view = view;
		view.setPresenter(this);
		this.sequencer = seqFactory;
	}

	private void displayPlace() {
		sequencer.withPlace(place)
				.withPanel(getPanel())
				.go();
	}

	private FlowPanel getPanel() {
		return view.getContentPanel();
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
