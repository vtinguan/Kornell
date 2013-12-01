package kornell.gui.client.presentation.course.details.generic;

import java.util.List;

import kornell.api.client.KornellClient;
import kornell.api.client.UserSession;
import kornell.core.lom.Actom;
import kornell.core.lom.ExternalPage;
import kornell.core.to.CourseClassTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.HistoryMapper;
import kornell.gui.client.presentation.course.CourseClassPlace;
import kornell.gui.client.presentation.course.details.CourseDetailsView;
import kornell.gui.client.sequence.CourseSequencer;
import kornell.gui.client.util.ClientProperties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericPageView extends Composite implements
		CourseDetailsView {
	interface MyUiBinder extends UiBinder<Widget, GenericPageView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private final HistoryMapper historyMapper = GWT.create(HistoryMapper.class);

	private KornellClient client;
	private PlaceController placeCtrl;
	private EventBus bus;
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private String IMAGES_PATH = "skins/first/icons/courseDetails/";

	@UiField
	FlowPanel topicWrapper;
	@UiField
	FlowPanel topicPanel;
	@UiField
	Image topicIcon;
	@UiField
	FlowPanel lblPage;

	private ExternalPage page;
	private List<Actom> actoms;
	private CourseClassTO currentCourse;
	private UserSession session;
	
	public GenericPageView(EventBus eventBus, KornellClient client,
			final PlaceController placeCtrl, UserSession session, final ExternalPage page, CourseClassTO currentCourse) {
		this.bus = eventBus;
		this.client = client;
		this.placeCtrl = placeCtrl;
		this.page = page;
		this.currentCourse = currentCourse;
		this.session = session;
		initWidget(uiBinder.createAndBindUi(this));
		display();
	}
	
	private void display() {
		topicIcon.setUrl(IMAGES_PATH + "status_toStart.png");

		Anchor pageAnchor = new Anchor(page.getTitle());
		pageAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String breadCrumb = CourseSequencer.class.getName() + "." + currentCourse.getCourseClass().getUUID() + ".CURRENT_KEY";
				session.setItem(breadCrumb, page.getKey());
				placeCtrl.goTo(new CourseClassPlace(currentCourse.getCourseClass().getUUID()));
			}
		});
		lblPage.add(pageAnchor);
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
	}
}
