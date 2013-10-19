package kornell.gui.client.presentation.course.details.generic;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.shared.data.Actom;
import kornell.core.shared.data.Content;
import kornell.core.shared.data.Contents;
import kornell.core.shared.data.ContentsCategory;
import kornell.core.shared.data.ExternalPage;
import kornell.core.shared.data.coursedetails.CertificationTO;
import kornell.core.shared.data.coursedetails.HintTO;
import kornell.core.shared.data.coursedetails.InfoTO;
import kornell.core.shared.data.coursedetails.TopicTO;
import kornell.core.shared.to.CourseTO;
import kornell.core.shared.to.UserInfoTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.HistoryMapper;
import kornell.gui.client.presentation.course.CoursePlace;
import kornell.gui.client.presentation.course.details.CourseDetailsPlace;
import kornell.gui.client.presentation.course.details.CourseDetailsView;
import kornell.gui.client.presentation.course.details.data.CourseDetailsTOBuilder;
import kornell.gui.client.sequence.CourseSequencer;
import kornell.gui.client.util.ClientProperties;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
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
	
	public GenericPageView(EventBus eventBus, KornellClient client,
			final PlaceController placeCtrl, final ExternalPage page) {
		this.bus = eventBus;
		this.client = client;
		this.placeCtrl = placeCtrl;
		this.page = page;
		initWidget(uiBinder.createAndBindUi(this));
		display();
	}
	
	private void display() {
		topicIcon.setUrl(IMAGES_PATH + "status_toStart.png");

		/*Anchor pageAnchor = new Anchor(page.getTitle());
		pageAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ClientProperties.set(CourseSequencer.class.getName() + ".CURRENT_KEY", page.getKey());
				placeCtrl.goTo(new CoursePlace("d9aaa03a-f225-48b9-8cc9-15495606ac46"));
			}
		});*/
		lblPage.add(new Label(page.getTitle()));
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
	}
}
