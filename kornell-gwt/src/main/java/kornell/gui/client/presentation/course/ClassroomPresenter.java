package kornell.gui.client.presentation.course;

import java.util.logging.Logger;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.lom.Contents;
import kornell.core.to.EnrollmentLaunchTO;
import kornell.gui.client.Kornell;
import kornell.gui.client.event.EnrollmentEvent;
import kornell.gui.client.personnel.Teachers;
import kornell.gui.client.sequence.Sequencer;
import kornell.gui.client.sequence.SequencerFactory;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class ClassroomPresenter implements ClassroomView.Presenter {
	Logger log = Logger.getLogger(Kornell.class.getName());
	private ClassroomView view;
	private ClassroomPlace place;
	private SequencerFactory sequencerFactory;
	private Contents contents;
	private Sequencer sequencer;
	private EventBus bus;
	private KornellSession session;

	public ClassroomPresenter(ClassroomView view,
			SequencerFactory seqFactory,
			EventBus bus,
			KornellSession session) {
		this.bus = bus;
		this.view = view;
		this.session = session;
		view.setPresenter(this);
		this.sequencerFactory = seqFactory;
	}

	private void displayPlace() {
		view.asWidget().setVisible(true);
		view.display(place);
	}

	private FlowPanel getPanel() {
		return view.getContentPanel();
	}

	@Override
	public void startSequencer() {
		sequencer = sequencerFactory.withPlace(place).withPanel(getPanel());
		sequencer.go(contents);
		session.enrollment(place.getEnrollmentUUID())
				.launch(new Callback<EnrollmentLaunchTO>() {
					@Override
					public void ok(EnrollmentLaunchTO to) {
						bus.fireEvent(new EnrollmentEvent(to));
					}
		});

	}

	@Override
	public Contents getContents() {
		return contents;
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	public void setPlace(ClassroomPlace place) {
		this.place = place;
		displayPlace();
	}

	@Override
	public void fireProgressEvent() {
		if (sequencer != null)
			sequencer.fireProgressEvent();
	}
}
