package kornell.gui.client.presentation.course.details.generic;

import kornell.api.client.KornellClient;
import kornell.api.client.UserSession;
import kornell.core.lom.Content;
import kornell.core.lom.ExternalPage;
import kornell.core.to.CourseClassTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.course.details.CourseDetailsView;

import com.github.gwtbootstrap.client.ui.Collapse;
import com.github.gwtbootstrap.client.ui.CollapseTrigger;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.event.HideEvent;
import com.github.gwtbootstrap.client.ui.event.HideHandler;
import com.github.gwtbootstrap.client.ui.event.ShowEvent;
import com.github.gwtbootstrap.client.ui.event.ShowHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericTopicView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericTopicView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private KornellClient client;
	private PlaceController placeCtrl;
	private EventBus bus;
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private String IMAGES_PATH = "skins/first/icons/courseDetails/";
	private Content content;
	private UserSession session;
	private CourseClassTO currentCourse;
	private int index;
	private boolean startOpened;
	private boolean enableAnchorOnFirstChild;
	
	@UiField
	CollapseTrigger trigger;
	@UiField
	FlowPanel topicWrapper;
	@UiField
	FlowPanel topicPanel;
	@UiField
	Image topicIcon;
	@UiField
	Label lblTopic;
	@UiField
	Collapse collapse;
	@UiField
	FluidRow childrenPanel;


	public GenericTopicView(EventBus eventBus, KornellClient client,
			PlaceController placeCtrl, UserSession session, CourseClassTO currentCourse, Content content, int index, boolean startOpened, boolean enableAnchorOnFirstChild) {
		this.bus = eventBus;
		this.client = client;
		this.placeCtrl = placeCtrl;
		this.session = session;
		this.content = content;
		this.currentCourse = currentCourse;
		this.index = index;
		this.startOpened = startOpened;
		this.enableAnchorOnFirstChild = enableAnchorOnFirstChild;
		initWidget(uiBinder.createAndBindUi(this));
		initData();
		collapse.addShowHandler(new ShowHandler() {
			@Override
			public void onShow(ShowEvent showEvent) {
				topicIcon.setUrl(IMAGES_PATH + "topic-expanded.png");
			}
		});
		collapse.addHideHandler(new HideHandler() {
			@Override
			public void onHide(HideEvent hideEvent) {
				topicIcon.setUrl(IMAGES_PATH + "topic-contracted.png");
			}
		});
		display();
	}

	private void initData() {
		
	}

	private void display() {
		String topicName = content.getTopic().getName();
		lblTopic.setText(topicName);
		
		trigger.setTarget("#toggle"+index);
		collapse.setId("toggle"+index);
		
		ExternalPage page;
		boolean isPreviousPageVisited = (index == 1);
		int childrenIndex = 0;
		for (Content contentItem : content.getTopic().getChildren()) {
			page = contentItem.getExternalPage();
			if(!page.getTitle().startsWith("###")){ //TODO MDA 
				boolean enableAnchor = page.isVisited() || isPreviousPageVisited || (childrenIndex == 0 && enableAnchorOnFirstChild);
				childrenPanel.add(new GenericPageView(bus, client, placeCtrl, session, page, currentCourse, enableAnchor));
			}
			isPreviousPageVisited = page.isVisited();
			childrenIndex++;
		}
		
		if(childrenPanel.getWidgetCount() > 0){
			this.addStyleName("cursorPointer");
		} else {
			topicIcon.addStyleName("shy");
			this.addStyleName("cursorDefault");
		}
		
		if(startOpened){
			topicIcon.setUrl(IMAGES_PATH + "topic-expanded.png");
			collapse.setDefaultOpen(true);
		} else {
			topicIcon.setUrl(IMAGES_PATH + "topic-contracted.png");
		}
	}
}
