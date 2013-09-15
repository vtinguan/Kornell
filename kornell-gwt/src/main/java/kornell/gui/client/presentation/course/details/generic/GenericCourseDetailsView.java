package kornell.gui.client.presentation.course.details.generic;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.shared.data.coursedetails.CertificationTO;
import kornell.core.shared.data.coursedetails.CourseDetailsTO;
import kornell.core.shared.data.coursedetails.HintTO;
import kornell.core.shared.data.coursedetails.InfoTO;
import kornell.core.shared.data.coursedetails.TopicTO;
import kornell.core.shared.to.CourseTO;
import kornell.core.shared.to.UserInfoTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.HistoryMapper;
import kornell.gui.client.presentation.atividade.AtividadePlace;
import kornell.gui.client.presentation.course.details.CourseDetailsPlace;
import kornell.gui.client.presentation.course.details.CourseDetailsView;
import kornell.gui.client.presentation.course.details.data.CourseDetailsTOBuilder;

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


public class GenericCourseDetailsView extends Composite  implements CourseDetailsView {
	interface MyUiBinder extends UiBinder<Widget, GenericCourseDetailsView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private final HistoryMapper historyMapper = GWT.create(HistoryMapper.class);


	private KornellClient client;
	private PlaceController placeCtrl;
	private EventBus bus;
	private KornellConstants constants = GWT.create(KornellConstants.class); 
	private String IMAGES_PATH = "skins/first/icons/courseDetails/";

	@UiField
	FlowPanel detailsPanel;
	@UiField
	FlowPanel titlePanel;
	@UiField
	FlowPanel buttonsPanel;
	@UiField
	FlowPanel contentPanel;

	@UiField
	Button btnAbout;
	@UiField
	Button btnTopics;
	@UiField
	Button btnCertification;

	Button btnCurrent;

	FlowPanel closePanel;

	CourseTO courseTO;

	CourseDetailsTO courseDetails;

	UserInfoTO user;

	public GenericCourseDetailsView(EventBus eventBus, KornellClient client, PlaceController placeCtrl) {
		this.bus = eventBus;
		this.client = client;
		this.placeCtrl = placeCtrl;
		initWidget(uiBinder.createAndBindUi(this));
		initData();		
	}

	private void initData() {
		String uuid = placeCtrl.getWhere() instanceof CourseDetailsPlace ?
				((CourseDetailsPlace) placeCtrl.getWhere()).getCourseUUID() :
					((AtividadePlace) placeCtrl.getWhere()).getCourseUUID();

				client.getCourseTO(uuid,new Callback<CourseTO>(){
					@Override
					protected void ok(CourseTO to) {
						courseTO = to;

						client.getCurrentUser(new Callback<UserInfoTO>() {
							@Override
							protected void ok(UserInfoTO userTO) {
								user = userTO;
								display();
							}
						});
					}			
				});
	}


	private void display() {
		CourseDetailsTOBuilder builder = new CourseDetailsTOBuilder(courseTO.getCourse().getInfoJson());
		builder.buildCourseDetails();
		courseDetails = builder.getCourseDetailsTO();

		btnCurrent = btnAbout;
		displayTitle();
		displayButtons();
		displayContent(btnCurrent);
	}

	private void displayContent(Button btn) {
		contentPanel.clear();
		if(btn.equals(btnAbout)){
			contentPanel.add(getInfosPanel());
			contentPanel.add(getHintsPanel());
		} else if(btn.equals(btnTopics)){
			contentPanel.add(getTopicsPanel());
		} else if(btn.equals(btnCertification)) {
			contentPanel.add(getCertificationPanel());
		}
	}

	private FlowPanel getCertificationPanel() {
		FlowPanel certificationPanel = new FlowPanel();
		certificationPanel.addStyleName("certificationPanel");

		certificationPanel.add(getCertificationInfo());
		certificationPanel.add(getCertificationTableHeader());
		certificationPanel.add(getCertificationTableContent());

		return certificationPanel;
	}

	private FlowPanel getCertificationInfo() {
		FlowPanel certificationInfo = new FlowPanel();
		certificationInfo.addStyleName("certificationInfo");

		Label infoTitle = new Label(courseDetails.getCertificationHeaderInfoTO().getType());
		infoTitle.addStyleName("certificationInfoTitle");
		certificationInfo.add(infoTitle);

		Label infoText = new Label(courseDetails.getCertificationHeaderInfoTO().getText());
		infoText.addStyleName("certificationInfoText");
		certificationInfo.add(infoText);

		return certificationInfo;
	}

	private FlowPanel getCertificationTableContent() {
		FlowPanel certificationContentPanel = new FlowPanel();
		certificationContentPanel.addStyleName("certificationContentPanel");

		for (CertificationTO certificationTO : courseDetails.getCertifications()) {
			certificationContentPanel.add(getCertificationWrapper(certificationTO));
		}

		return certificationContentPanel;
	}

	private FlowPanel getCertificationWrapper(CertificationTO certificationTO) {
		FlowPanel certificationWrapper = new FlowPanel();
		certificationWrapper.addStyleName("certificationWrapper");

		FlowPanel itemPanel = new FlowPanel();
		itemPanel.addStyleName("itemPanel");

		Image certificationIcon = new Image(IMAGES_PATH + certificationTO.getType() + ".png");
		certificationIcon.addStyleName("certificationIcon");
		itemPanel.add(certificationIcon);

		Label lblName = new Label(certificationTO.getName());
		lblName.addStyleName("lblName");
		itemPanel.add(lblName);

		Label lblDescription = new Label(certificationTO.getDescription());
		lblDescription.addStyleName("lblDescription");
		itemPanel.add(lblDescription);

		certificationWrapper.add(itemPanel);

		Label lblStatus = new Label(certificationTO.getStatus());
		lblStatus.addStyleName("lblStatus");
		certificationWrapper.add(lblStatus);

		Label lblGrade = new Label("certification".equals(certificationTO.getType()) ? " " : (!"".equals(certificationTO.getGrade()) ? certificationTO.getGrade() : "-"));
		lblGrade.addStyleName("lblGrade");
		certificationWrapper.add(lblGrade);

		Anchor lblActions;
		if("test".equals(certificationTO.getType())){
			lblActions = new Anchor("Visualizar");
		}
		else if("certification".equals(certificationTO.getType())){
			lblActions = new Anchor("Gerar");
			lblActions.addClickHandler(new ClickHandler() {
				@Override
				public void onClick (ClickEvent event){
					Window.open(client.getApiUrl() + "/report/certificate/" + user.getPerson().getUUID() + "/" + courseTO.getCourse().getUUID() , "", "");
				}
			});
		} else {
			lblActions = new Anchor("-");
		}
		lblActions.addStyleName("lblActions");
		certificationWrapper.add(lblActions);

		return certificationWrapper;
	}

	private FlowPanel getCertificationTableHeader() {
		FlowPanel certificationHeaderPanel = new FlowPanel(); 
		certificationHeaderPanel.addStyleName("certificationHeaderPanel");

		certificationHeaderPanel.add(getHeaderButton("Item", "btnItem", "btnCertificationHeader"));
		certificationHeaderPanel.add(getHeaderButton("Status", "btnStatus", "btnCertificationHeader"));
		certificationHeaderPanel.add(getHeaderButton("Nota", "btnGrade", "btnCertificationHeader"));
		certificationHeaderPanel.add(getHeaderButton("Ações", "btnActions", "btnCertificationHeader"));

		return certificationHeaderPanel;
	}

	private FlowPanel getTopicsPanel() {
		FlowPanel topicsPanel = new FlowPanel();
		topicsPanel.addStyleName("topicsPanel");

		topicsPanel.add(getTopicsTableHeader());
		topicsPanel.add(getTopicsTableContent());

		return topicsPanel;
	}

	private FlowPanel getTopicsTableContent() {
		FlowPanel topicsContentPanel = new FlowPanel();
		topicsContentPanel.addStyleName("topicsContentPanel");

		for (TopicTO topicTO : courseDetails.getTopics()) {
			topicsContentPanel.add(getTopicWrapper(topicTO));
		}

		return topicsContentPanel;
	}

	private FlowPanel getTopicWrapper(TopicTO topicTO) {
		FlowPanel topicWrapper = new FlowPanel();
		topicWrapper.addStyleName("topicWrapper");

		FlowPanel topicPanel = new FlowPanel();
		topicPanel.addStyleName("topicPanel");

		Image topicIcon = new Image(IMAGES_PATH + "status_" + topicTO.getType() + ".png");
		topicIcon.addStyleName("topicIcon");
		topicPanel.add(topicIcon);

		Label lblTopic = new Label(topicTO.getTitle());
		lblTopic.addStyleName("lblTopic");
		topicPanel.add(lblTopic);

		topicWrapper.add(topicPanel);
		/*
		Label lblStatus = new Label(topicTO.getStatus());
		lblStatus.addStyleName("finishedTest".equals(topicTO.getType()) ? "lblStatusFinishedTest" : "lblStatus");
		topicWrapper.add(lblStatus);

		Label lblTime = new Label("".equals(topicTO.getTime()) ? "-" : topicTO.getTime());
		lblTime.addStyleName("lblTime");
		topicWrapper.add(lblTime);

		FlowPanel pnlForumComments = new FlowPanel();
		pnlForumComments.addStyleName("pnlForumComments");

		Label lblForumComments = new Label(topicTO.getForumComments() == 0 ? "-" : ""+topicTO.getForumComments());
		lblForumComments.addStyleName("lblForumComments");
		pnlForumComments.add(lblForumComments);

		if(topicTO.getNewForumComments() > 0){
			Label lblDividingBar = new Label("|");
			lblDividingBar.addStyleName("lblDividingBar");
			pnlForumComments.add(lblDividingBar);

			Label lblNew = new Label("Novos:");
			lblNew.addStyleName("lblNew");
			pnlForumComments.add(lblNew);

			Label lblNewForumComments = new Label(""+topicTO.getNewForumComments());
			lblNewForumComments.addStyleName("lblNewForumComments");
			pnlForumComments.add(lblNewForumComments);
		}

		topicWrapper.add(pnlForumComments);

		Label lblNotes = new Label(topicTO.isNotes() ? "Ver" : "-");
		lblNotes.addStyleName("lblNotes");
		topicWrapper.add(lblNotes);
		 */
		return topicWrapper;
	}

	private FlowPanel getTopicsTableHeader() {
		FlowPanel topicsHeaderPanel = new FlowPanel(); 
		topicsHeaderPanel.addStyleName("topicsHeaderPanel");

		topicsHeaderPanel.add(getHeaderButton(constants.topic(), "btnTopics", "btnTopicsHeader"));
		/*topicsHeaderPanel.add(getHeaderButton("Status", "btnStatus", "btnTopicsHeader"));
		topicsHeaderPanel.add(getHeaderButton("Tempo", "btnTime", "btnTopicsHeader"));
		topicsHeaderPanel.add(getHeaderButton("Comentários no Fórum", "btnForumComments", "btnTopicsHeader"));
		topicsHeaderPanel.add(getHeaderButton("Anotações", "btnNotes", "btnTopicsHeader"));*/

		return topicsHeaderPanel;
	}

	private Button getHeaderButton(String label, String styleName, String styleNameGlobal) {
		Button btn = new Button(label);
		btn.removeStyleName("btn");
		btn.addStyleName(styleNameGlobal); 
		btn.addStyleName(styleName);
		btn.addStyleName("btnNotSelected");
		return btn;
	}

	private void displayTitle() {		
		Image titleImage = new Image(IMAGES_PATH + "details.png");
		titleImage.addStyleName("titleImage");
		titlePanel.add(titleImage);

		Label titleLabel = new Label(constants.detailsHeader()+" ");
		titleLabel.addStyleName("titleLabel");
		titlePanel.add(titleLabel);

		Label courseNameLabel = new Label(courseTO.getCourse().getTitle());
		courseNameLabel.addStyleName("courseNameLabel");
		titlePanel.add(courseNameLabel);	

		closePanel = new FlowPanel();
		closePanel.setStyleName("closePanel");

		Image closeImage = new Image(IMAGES_PATH + "close.png");
		closeImage.addStyleName("closeImage");
		closeImage.addClickHandler(new DetailsCloseClickHandler());
		closePanel.add(closeImage);

		Label closeLabel = new Label(constants.closeDetails());
		closeLabel.addStyleName("closeLabel");
		closeLabel.addClickHandler(new DetailsCloseClickHandler());
		closePanel.add(closeLabel);


		titlePanel.add(closePanel);
	}

	private FlowPanel getInfosPanel() {
		FlowPanel infoPanel = new FlowPanel();
		infoPanel.addStyleName("infoPanel");
		for (InfoTO infoTO : courseDetails.getInfos()) {
			infoPanel.add(getInfoPanel(infoTO.getType(), infoTO.getText()));
		}
		return infoPanel;
	}

	private FlowPanel getInfoPanel(String title, String text) {
		FlowPanel info = new FlowPanel();
		info.addStyleName("infoDetails");

		Label infoTitle = new Label(title);
		infoTitle.addStyleName("infoTitle");
		info.add(infoTitle);

		Label infoText = new Label(text);
		infoText.addStyleName("infoText");
		info.add(infoText);

		return info;
	}

	private void displayButtons() {		
		displayButton(btnAbout, constants.btnAbout(), constants.btnAboutInfo());
		displayButton(btnTopics, constants.btnTopics(), constants.btnTopicsInfo());
		displayButton(btnCertification, constants.btnCertification(), constants.btnCertificationInfo());
	}

	private void displayButton(Button btn, String title, String label) {
		btn.removeStyleName("btn");

		Label btnTitle = new Label(title);
		btnTitle.addStyleName("btnTitle");
		btn.add(btnTitle);

		Label btnLabel = new Label(label);
		btnLabel.addStyleName("btnLabel");
		btn.add(btnLabel);

		btn.addClickHandler(new DetailsButtonClickHandler());
	}

	private FlowPanel getHintsPanel() {
		FlowPanel hintsPanel = new FlowPanel();
		hintsPanel.addStyleName("hintsPanel");

		for (HintTO hintTO : courseDetails.getHints()) {
			hintsPanel.add(getHintPanel(hintTO.getType(), hintTO.getName()));
		}

		return hintsPanel;
	}

	private FlowPanel getHintPanel(String img, String hintText) {
		FlowPanel hint = new FlowPanel();
		hint.addStyleName("hintDetails");

		Image hintImg = new Image(IMAGES_PATH + img + ".png");
		hintImg.addStyleName("hintImg");
		hint.add(hintImg);

		Label lblHintText = new Label(hintText);
		lblHintText.addStyleName("hintText");
		hint.add(lblHintText);

		return hint;
	}

	private void handleEvent(Button btn) {		
		btnCurrent.removeStyleName("btnSelected");
		btnCurrent.addStyleName("btnNotSelected");
		btn.addStyleName("btnSelected");
		btn.removeStyleName("btnNotSelected");

		displayContent(btn);
		btnCurrent = btn;
	}

	private final class DetailsButtonClickHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			handleEvent((Button) event.getSource());
		}
	}

	private final class DetailsCloseClickHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			if(placeCtrl.getWhere() instanceof AtividadePlace){
				placeCtrl.goTo(new CourseDetailsPlace(getCourseUUID()));
			} else {
				client.getCurrentUser(new Callback<UserInfoTO>() {
					@Override
					protected void ok(UserInfoTO userTO) {
						user = userTO;
						placeCtrl.goTo(historyMapper.getPlace(user.getLastPlaceVisited()));
					}
				});
			}
		}
	}

	private String getCourseUUID() {
		try{		
			return Window.Location.getHash().split(":")[1].split(";")[0];
		} catch (Exception ex){
			GWT.log("Error trying to get the course id.");
			placeCtrl.goTo(historyMapper.getPlace(user.getLastPlaceVisited()));
		}
		return null;
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}
}



