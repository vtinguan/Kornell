package kornell.gui.client.presentation.course.details.generic;

import kornell.api.client.KornellClient;
import kornell.core.lom.Content;
import kornell.core.lom.ExternalPage;
import kornell.core.to.CourseTO;
import kornell.core.to.UserInfoTO;
import kornell.core.to.coursedetails.CourseDetailsTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.HistoryMapper;
import kornell.gui.client.presentation.course.details.CourseDetailsView;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Collapse;
import com.github.gwtbootstrap.client.ui.CollapseTrigger;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.Paragraph;
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

public class GenericTopicView extends Composite implements
		CourseDetailsView {
	interface MyUiBinder extends UiBinder<Widget, GenericTopicView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private final HistoryMapper historyMapper = GWT.create(HistoryMapper.class);

	private KornellClient client;
	private PlaceController placeCtrl;
	private EventBus bus;
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private String IMAGES_PATH = "skins/first/icons/courseDetails/";
	private Content content;
	private Button btnCurrent;
	private CourseTO courseTO;
	private CourseDetailsTO courseDetails;
	private UserInfoTO user;
	private int index;
	
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
			PlaceController placeCtrl, Content content, int index) {
		this.bus = eventBus;
		this.client = client;
		this.placeCtrl = placeCtrl;
		this.content = content;
		this.index = index;
		initWidget(uiBinder.createAndBindUi(this));
		initData();
		display();
	}

	private void initData() {
		
	}

	private void display() {
		String topicName = content.getTopic().getName();
		lblTopic.setText(topicName);
		
		trigger.setTarget("#toggle"+index);
		collapse.setId("toggle"+index);
		
		for (Content contentItem : content.getTopic().getChildren()) {
			ExternalPage page = contentItem.getExternalPage();
			if(!page.getTitle().startsWith("###")){ //TODO MDA
				childrenPanel.add(new GenericPageView(bus, client, placeCtrl, page));
			}
		}
		
		if(childrenPanel.getWidgetCount() > 0){
			topicIcon.setUrl(IMAGES_PATH + "topic-expand.png");
		} else {
			topicIcon.addStyleName("shy");
		}
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}
}
