package kornell.gui.client.sequence;

import java.util.logging.Logger;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.lom.Contents;
import kornell.core.scorm.scorm12.rte.action.OpenSCO12Action;
import kornell.core.to.ActionTO;
import kornell.core.to.ActionType;
import kornell.core.to.EnrollmentLaunchTO;
import kornell.gui.client.event.EnrollmentEvent;
import kornell.gui.client.event.EnrollmentEventHandler;
import kornell.gui.client.presentation.course.ClassroomPlace;
import kornell.gui.client.uidget.OpenURLView;
import kornell.scorm.client.scorm12.SCORM12Adapter;
import kornell.scorm.client.scorm12.SCORM12Binder;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Follow server-side evaluation and actions.
 */
public class ThinSequencer 
	implements Sequencer, EnrollmentEventHandler {
	public static final Logger log = Logger.getLogger(ThinSequencer.class
			.getName());
	private FlowPanel contentPanel;
	private ClassroomPlace place;
	private EventBus bus;
	private KornellSession session;

	public ThinSequencer(EventBus bus, KornellSession session) {
		this.bus = bus;
		this.session = session;
		bus.addHandler(EnrollmentEvent.TYPE, this);
	}

	@Override
	public void onContinue(NavigationRequest event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPrevious(NavigationRequest event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDirect(NavigationRequest event) {
		// TODO Auto-generated method stub

	}

	@Override
	public Sequencer withPanel(FlowPanel contentPanel) {
		this.contentPanel = contentPanel;
		return this;
	}

	@Override
	public Sequencer withPlace(ClassroomPlace place) {
		this.place = place;
		return this;
	}

	@Override
	// TODO: 000 Deprecate "contents"
	public void go(Contents contents) {
		log.severe("deprecated go(Contents)");
	}

	private Widget createWidgetForAction(ActionTO actionTO) {
		ActionType type = actionTO.getType();
		Widget widget = null;
		switch (type) {
		case OpenSCO12:
			widget = openSCO(actionTO.getOpenSCO12Action());
			break;
		default:
			throw new IllegalArgumentException("Unknown action type " + type);
		}
		return widget;
	}

	private Widget openSCO(OpenSCO12Action openSCO) {
		SCORM12Adapter api = new SCORM12Adapter(bus, session,
				place.getEnrollmentUUID(), openSCO);
		SCORM12Binder.bind(api);
		return new OpenURLView(openSCO.getHref());
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void fireProgressEvent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnrollmentLaunched(EnrollmentLaunchTO launchTO) {
		Widget view = createWidgetForAction(launchTO.getActionTO());
		contentPanel.clear();
		contentPanel.add(view);
	}

}
