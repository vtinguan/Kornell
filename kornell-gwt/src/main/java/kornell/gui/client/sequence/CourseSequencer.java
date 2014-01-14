package kornell.gui.client.sequence;

import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.api.client.UserSession;
import kornell.core.lom.Actom;
import kornell.core.lom.Contents;
import kornell.core.lom.ContentsCategory;
import kornell.core.lom.ExternalPage;
import kornell.gui.client.event.ProgressChangeEvent;
import kornell.gui.client.event.ProgressChangeEventHandler;
import kornell.gui.client.event.ViewReadyEvent;
import kornell.gui.client.event.ViewReadyEventHandler;
import kornell.gui.client.presentation.course.CourseClassPlace;
import kornell.gui.client.uidget.ExternalPageView;
import kornell.gui.client.uidget.Uidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.web.bindery.event.shared.EventBus;

public class CourseSequencer implements Sequencer {

	private FlowPanel contentPanel;
	private String courseClassUUID;
	private KornellClient client;
	private EventBus bus;
	private List<Actom> actoms;

	private int currentIndex;
	private Actom currentActom;

	private Actom nextActom;
	private Uidget nextUidget;
	private Uidget currentUidget;
	private Actom prevActom;
	private Uidget prevUidget;

	public CourseSequencer(EventBus bus, KornellClient client) {
		this.client = client;
		this.bus = bus;
		bus.addHandler(NavigationRequest.TYPE, this);
	}

	@Override
	public void onContinue(NavigationRequest event) {
		if (doesntHaveNext())
			return;
		removePrevious();
		makeCurrentPrevious();
		makeNextCurrent();
		currentIndex++;
		preloadNext();
		makeCurrentVisible();
		dropBreadcrumb();
		debug("CONTINUED");
	}

	@Override
	public void onPrevious(NavigationRequest event) {
		if (doesntHavePrev())
			return;
		removeNext();
		makeCurrentNext();
		makePrevCurrent();
		currentIndex--;
		preloadPrev();
		makeCurrentVisible();
		dropBreadcrumb();
		debug("PREVED");
	}

	private String getBreadcrumbKey() {
		return CourseSequencer.class.getName() + "." + courseClassUUID
				+ ".CURRENT_KEY";
	}

	private void debug(String event) {
		String prevString = prevKey() + prevVis();
		String currString = currentKey() + currVis();
		String nextString = nextKey() + nextVis();

		// TODO: Use GWT Logging properly
		// GWT.log(event + " " + currentIndex + " [" + prevString + " | " +
		// currString + " | " + nextString + "]");

	}

	private String nextVis() {
		return " " + (nextUidget != null ? nextUidget.isVisible() : "-");
	}

	private String currVis() {
		return " " + (currentUidget != null ? currentUidget.isVisible() : "-");
	}

	private String prevVis() {
		return " " + (prevUidget != null ? prevUidget.isVisible() : "-");
	}

	private String currentKey() {
		return currentActom != null ? currentActom.getKey() : "";
	}

	private String prevKey() {
		return prevActom != null ? prevActom.getKey() : "";
	}

	private String nextKey() {
		return nextActom != null ? nextActom.getKey() : "";
	}

	private boolean doesntHaveNext() {
		return actoms != null && currentIndex >= actoms.size() - 1;
	}

	private boolean doesntHavePrevious() {
		return actoms != null && currentIndex <= 1;
	}

	private void makeCurrentPrevious() {
		prevActom = currentActom;
		prevUidget = currentUidget;
	}

	private void removePrevious() {
		if (contentPanel.getWidgetIndex(prevUidget) == -1)
			return;

		contentPanel.remove(prevUidget);
		prevActom = null;
	}

	private void makeNextCurrent() {
		currentActom = nextActom;
		currentUidget = nextUidget;
	}

	private void makeCurrentVisible() {
		if (currentUidget != null)
			currentUidget.setVisible(true);
		else
			GWT.log("CURRENT UIDGET IS NULL. HOW COME?");
		if (nextUidget != null)
			nextUidget.setVisible(false);
		if (prevUidget != null)
			prevUidget.setVisible(false);
	}

	private void dropBreadcrumb() {
		UserSession.current(new Callback<UserSession>() {
			@Override
			public void ok(UserSession session) {
				session.setItem(getBreadcrumbKey(), currentKey());
				String personUUID = session.getPersonUUID();
				client.events().actomEntered(personUUID, courseClassUUID, currentActom.getKey()).fire();
				currentActom.setVisited(true);
				fireProgressChangeEvent();
			}
		});
	}

	private boolean doesntHavePrev() {
		return currentIndex == 0;
	}

	private void preloadPrev() {
		if (currentIndex > 0) {
			prevActom = actoms.get(currentIndex - 1);
			prevUidget = uidgetFor(prevActom);
			prevUidget.setVisible(false);
			contentPanel.add(prevUidget);
		} else {
			prevActom = null;
			prevUidget = null;
		}
	}

	private void makeCurrentNext() {
		nextUidget = currentUidget;
		nextActom = currentActom;
	}

	private void removeNext() {
		if (nextUidget != null)
			contentPanel.remove(nextUidget);
		nextActom = null;
	}

	private void makePrevCurrent() {
		currentActom = prevActom;
		currentUidget = prevUidget;
	}

	@Override
	public Sequencer withPanel(FlowPanel contentPanel) {
		this.contentPanel = contentPanel;
		contentPanel.clear();
		return this;
	}

	@Override
	public Sequencer withPlace(CourseClassPlace place) {
		this.courseClassUUID = place.getCourseClassUUID();
		return this;
	}

	@Override
	public void go() {
		client.courseClass(courseClassUUID).contents(new Callback<Contents>() {

			@Override
			public void ok(Contents contents) {
				setContents(contents);
				orientateAndSail();
			}

			private void orientateAndSail() {
				UserSession.current(new Callback<UserSession>() {
					@Override
					public void ok(UserSession session) {
						String currentKey = session.getItem(getBreadcrumbKey());
						currentIndex = lookupCurrentIndex(currentKey);
						currentActom = actoms.get(currentIndex);
						initialLoad();
					}
				});
			}

			private int lookupCurrentIndex(String currentKey) {
				int currentIndex = 0;
				if (currentKey != null && !currentKey.isEmpty()) {
					for (int i = 0; i < actoms.size(); i++) {
						Actom actom = actoms.get(i);
						if (currentKey.equals(actom.getKey())) {
							return i;
						}
					}
				}
				return currentIndex;
			}

		});
	}

	class ShowWhenReady implements ViewReadyEventHandler {
		private Uidget uidget;

		public ShowWhenReady(Uidget uidget) {
			this.uidget = uidget;
		}

		@Override
		public void onViewReady(ViewReadyEvent evt) {
			uidget.setVisible(true);
		}
	}

	private void initialLoad() {
		showCurrentASAP();
		preloadNext();
		preloadPrevious();
		debug("INITIAL");
	}

	private void preloadNext() {
		if (doesntHaveNext()) {
			nextActom = null;
			nextUidget = null;
		} else {
			int nextIndex = currentIndex + 1;
			nextActom = actoms.get(nextIndex);
			nextUidget = uidgetFor(nextActom);
			nextUidget.setVisible(false);
			contentPanel.add(nextUidget);
		}
	}

	private void preloadPrevious() {
		if (doesntHavePrevious()) {
			prevActom = null;
			prevUidget = null;
		} else {
			int previousIndex = currentIndex - 1;
			prevActom = actoms.get(previousIndex);
			prevUidget = uidgetFor(prevActom);
			prevUidget.setVisible(false);
			contentPanel.add(prevUidget);
		}
	}

	private void showCurrentASAP() {
		currentUidget = uidgetFor(currentActom);
		contentPanel.add(currentUidget);
		currentUidget.setVisible(false);
		currentUidget.onViewReady(new ShowWhenReady(currentUidget));
		dropBreadcrumb();
	}

	private Uidget uidgetFor(Actom actom) {
		if (actom == null)
			return null;
		if (actom instanceof ExternalPage)
			return new ExternalPageView(client, courseClassUUID,
					(ExternalPage) actom);
		throw new IllegalArgumentException("Do not know how to view [" + actom
				+ "]");
	}

	private void setContents(Contents contents) {
		this.actoms = ContentsCategory.collectActoms(contents);
	}

	private void fireProgressChangeEvent() {
		int pagesVisitedCount = 0;
		int totalPages = actoms.size();
		for (Actom actom : actoms) {
			if(actom.isVisited()){
				pagesVisitedCount++;
				continue;
			}
			break;
		}
		ProgressChangeEvent progressChangeEvent = new ProgressChangeEvent();
		progressChangeEvent.setCurrentPage(currentIndex+1);
		progressChangeEvent.setTotalPages(totalPages);		
		progressChangeEvent.setPagesVisitedCount(pagesVisitedCount);
		progressChangeEvent.setCourseClassUUID(courseClassUUID);
		bus.fireEvent(progressChangeEvent);
	}
}