package kornell.scorm.client.scorm12;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.lom.Actom;
import kornell.core.lom.Contents;
import kornell.core.lom.ContentsOps;
import kornell.core.to.UserInfoTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.event.ProgressEvent;
import kornell.gui.client.presentation.course.ClassroomPlace;
import kornell.gui.client.sequence.NavigationRequest;
import kornell.gui.client.sequence.Sequencer;
import kornell.gui.client.sequence.SimpleSequencer;
import kornell.gui.client.uidget.Uidget;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.web.bindery.event.shared.EventBus;

public class SCORM12Sequencer extends SimpleSequencer implements Sequencer {

	@SuppressWarnings("unused")
	private ClassroomPlace place;
	private FlowPanel contentPanel;
	private Uidget currentUidget;

	public SCORM12Sequencer(EventBus bus, KornellSession session) {
		super(bus, session);
	}

	@Override
	public void onContinue(NavigationRequest event) {
		currentIndex++;
		paintCurrent();
		dropBreadcrumb();
	}

	private void makeCurrentVisible() {
		currentUidget.setVisible(true);
	}

	@Override
	public void onPrevious(NavigationRequest event) {
		currentIndex--;
		paintCurrent();
		dropBreadcrumb();
	}

	@Override
	public void onDirect(NavigationRequest event) {
		launch(event.getDestination());
	}

	private void launch(String key) {
		currentIndex = StringUtils.isSome(key) ? lookupCurrentIndex(key) : 0;
		currentActom = actoms.get(currentIndex);
		paintCurrent();
	}

	@Override
	public Sequencer withPanel(FlowPanel contentPanel) {
		this.contentPanel = contentPanel;
		return this;
	}

	@Override
	public Sequencer withPlace(ClassroomPlace place) {
		this.place = place;
		setEnrollmentUUID(place.getEnrollmentUUID());
		return this;
	}

	@Override
	public void go(Contents contents) {
		setContents(contents);
		session.getCurrentUser(new Callback<UserInfoTO>() {
			@Override
			public void ok(UserInfoTO userInfo) {
				String currentKey = session.getItem(getBreadcrumbKey());
				launch(currentKey);
			}
		});
	}

	private void paintCurrent() {
		if (contentPanel != null)
			contentPanel.clear();
		currentActom = actoms.get(currentIndex);
		currentUidget = Uidget.forActom(currentActom);
		currentUidget.setVisible(false);
		contentPanel.add(currentUidget);
		dropBreadcrumb();
		makeCurrentVisible();
	}

	private void setContents(Contents contents) {
		this.actoms = ContentsOps.collectActoms(contents);
	}

	@Override
	public void stop() {
		GWT.log("STOP");
	}

	// TODO: Smell - Activity Bar shows only after this fires.
	@Override
	public void fireProgressEvent() {
		GWT.log("FIRE!");
		if (actoms == null)
			return;
		int pagesVisitedCount = 0;
		int totalPages = actoms.size();
		for (Actom actom : actoms) {
			if (actom.isVisited()) {
				pagesVisitedCount++;
				continue;
			}
			break;
		}
		ProgressEvent progressEvent = new ProgressEvent();
		progressEvent.setCurrentPage(currentIndex + 1);
		progressEvent.setTotalPages(totalPages);
		progressEvent.setPagesVisitedCount(pagesVisitedCount);
		progressEvent.setEnrollmentUUID(place.getEnrollmentUUID());
		bus.fireEvent(progressEvent);
	}

}
