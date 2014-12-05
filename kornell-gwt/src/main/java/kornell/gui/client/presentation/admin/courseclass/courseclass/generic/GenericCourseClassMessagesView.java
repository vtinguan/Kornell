package kornell.gui.client.presentation.admin.courseclass.courseclass.generic;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.KornellSession;
import kornell.core.entity.CourseClass;
import kornell.core.to.CourseClassTO;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.admin.courseclass.courseclass.AdminCourseClassView.Presenter;
import kornell.gui.client.presentation.message.MessagePresenter;
import kornell.gui.client.presentation.message.MessageView;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.util.view.formfield.KornellFormFieldWrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericCourseClassMessagesView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericCourseClassMessagesView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private EventBus bus;
	private KornellSession session;
	PlaceController placeCtrl;
	ViewFactory viewFactory;
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private FormHelper formHelper;
	private boolean isInstitutionAdmin;
	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;

	private MessagePresenter presenter;

	@UiField
	FlowPanel messagesPanel;

	private UserInfoTO user;
	private CourseClassTO courseClassTO;
	private CourseClass courseClass;
	private FileUpload fileUpload;
	private List<KornellFormFieldWrapper> fields;
	
	public GenericCourseClassMessagesView(final KornellSession session, EventBus bus, PlaceController placeCtrl, ViewFactory viewFactory,
			MessagePresenter presenter, CourseClassTO courseClassTO) {
		this.session = session;
		this.bus = bus;
		this.placeCtrl = placeCtrl;
		this.viewFactory = viewFactory;
		this.presenter = presenter;
		this.user = session.getCurrentUser();
		formHelper = new FormHelper();
		initWidget(uiBinder.createAndBindUi(this));		
		this.courseClassTO = courseClassTO;		
		initData();
	}

	public void initData() {
		messagesPanel.setVisible(false);
		this.fields = new ArrayList<KornellFormFieldWrapper>();
		courseClass =  courseClassTO.getCourseClass();
		isInstitutionAdmin = session.isInstitutionAdmin();


		messagesPanel.add(getReportPanel());
		messagesPanel.setVisible(true);

	}

	private FlowPanel getReportPanel() {
		FlowPanel reportPanel = new FlowPanel();
		reportPanel.addStyleName("reportPanel");
		// TODO: i18n
		reportPanel.add(getMessagesInfo());
		reportPanel.add(getMessagesContent());

		return reportPanel;
	}

	private FlowPanel getMessagesInfo() {
		FlowPanel messagesInfo = new FlowPanel();
		messagesInfo.addStyleName("titlePanel");

		Label infoTitle = new Label("Mensagens");
		infoTitle.addStyleName("title");
		messagesInfo.add(infoTitle);

		Label infoText = new Label("Mensagens entre os alunos e os responsáveis pela turma.");
		infoText.addStyleName("subTitle");
		messagesInfo.add(infoText);

		return messagesInfo;
	}

	private FlowPanel getMessagesContent() {
		FlowPanel reportContentPanel = new FlowPanel();
		reportContentPanel.add(presenter.asWidget());
		return reportContentPanel;
	}

}