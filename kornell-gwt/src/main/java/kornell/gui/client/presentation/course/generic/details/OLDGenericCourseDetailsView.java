package kornell.gui.client.presentation.course.generic.details;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentState;
import kornell.core.lom.Actom;
import kornell.core.lom.Content;
import kornell.core.lom.ContentFormat;
import kornell.core.lom.Contents;
import kornell.core.lom.ContentsOps;
import kornell.core.lom.ExternalPage;
import kornell.core.to.CourseClassTO;
import kornell.core.to.EnrollmentTO;
import kornell.core.to.LibraryFilesTO;
import kornell.core.to.UserInfoTO;
import kornell.core.to.coursedetails.CourseDetailsTO;
import kornell.core.to.coursedetails.HintTO;
import kornell.core.to.coursedetails.InfoTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.event.ProgressEvent;
import kornell.gui.client.event.ShowDetailsEvent;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.course.ClassroomPlace;
import kornell.gui.client.presentation.course.ClassroomView.Presenter;
import kornell.gui.client.presentation.util.LoadingPopup;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class OLDGenericCourseDetailsView /*extends Composite*/ {
	/*
	interface MyUiBinder extends UiBinder<Widget, OLDGenericCourseDetailsView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
*/
	private KornellSession session;
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
	FlowPanel detailsContentPanel;

	private Button btnAbout;
	private Button btnTopics;
	private Button btnCertification;
	private Button btnLibrary;
	private Button btnGoToCourse;

	private Button btnCurrent;
	private CourseClassTO courseClassTO;
	private CourseDetailsTO courseDetails;
	private FlowPanel aboutPanel;
	private FlowPanel topicsPanel;
	private FlowPanel certificationPanel;
	private FlowPanel libraryPanel;

	private Presenter presenter;

	private Contents contents;
	private List<Actom> actoms;

	private boolean isEnrolled, isCancelled, isInactiveCourseClass;

	public OLDGenericCourseDetailsView(EventBus bus, KornellSession session,
			PlaceController placeCtrl) {
		this.bus = bus;
		this.session = session;
		this.placeCtrl = placeCtrl;
		//initWidget(uiBinder.createAndBindUi(this));
	}

	public void initData() {
		setContents(presenter.getContents());
		certificationPanel = getCertificationPanel();
		courseClassTO = Dean.getInstance().getCourseClassTO();
		display();
	}

	private void setContents(Contents contents) {
		this.contents = contents;
		this.actoms = ContentsOps.collectActoms(contents);
		// fireProgressChangeEvent();
	}

	private void fireProgressChangeEvent() {
		int pagesVisitedCount = 0;
		int totalPages = actoms.size();
		for (Actom actom : actoms) {
			if (actom.isVisited()) {
				pagesVisitedCount++;
				continue;
			}
			break;
		}
		ProgressEvent progressChangeEvent = new ProgressEvent();
		progressChangeEvent.setCurrentPage(0);
		progressChangeEvent.setTotalPages(totalPages);
		progressChangeEvent.setPagesVisitedCount(pagesVisitedCount);
		progressChangeEvent.setEnrollmentUUID(Dean.getInstance()
				.getCourseClassTO().getCourseClass().getUUID());
		bus.fireEvent(progressChangeEvent);
	}

	private void display() {
		isEnrolled = false;
		isCancelled = false;
		UserInfoTO user = session.getCurrentUser();
		Enrollment enrollment;
		/*
		for (EnrollmentTO enrollmentTO : user.getEnrollmentsTO()
				.getEnrollmentTOs()) {
			enrollment = enrollmentTO.getEnrollment();
			if (enrollment.getUUID()
					.equals(((ClassroomPlace) placeCtrl.getWhere())
							.getEnrollmentUUID())) {
				if (EnrollmentState.enrolled.equals(enrollment.getState())) {
					isEnrolled = true;
				} else if (EnrollmentState.cancelled.equals(enrollment
						.getState())) {
					isCancelled = true;
				}
			}
		}
		*/
		isInactiveCourseClass = false; // TODO: 000 Review
		// CourseClassState.inactive.equals(courseClassTO.getCourseClass().getState());
		displayButtons();
		if (courseClassTO != null) {
			CourseDetailsTOBuilder builder = new CourseDetailsTOBuilder(
					courseClassTO.getCourseVersionTO().getCourse()
							.getInfoJson());
			builder.buildCourseDetails();
			courseDetails = builder.getCourseDetailsTO();
		}
		topicsPanel = new FlowPanel();

		aboutPanel = getAboutPanel();
		detailsContentPanel.add(aboutPanel);
		btnCurrent = btnAbout;
		displayContent(btnCurrent);

		topicsPanel.addStyleName("topicsPanel");
		displayTopics();

		displayTitle();

		detailsContentPanel.add(topicsPanel);
		detailsContentPanel.add(certificationPanel);

		btnLibrary.setVisible(false);
		CourseClassTO courseClassTO2 = Dean.getInstance().getCourseClassTO();
		if (courseClassTO2 != null)
			session.courseClass(
					courseClassTO2.getCourseClass()
							.getUUID()).libraryFiles(
					new Callback<LibraryFilesTO>() {
						@Override
						public void ok(LibraryFilesTO to) {
							libraryPanel = getLibraryPanel(to);
							libraryPanel.setVisible(false);
							detailsContentPanel.add(libraryPanel);
							btnLibrary.setVisible(true);
						}
					});
	}

	private void displayContent(Button btn) {
		aboutPanel.setVisible(btn.equals(btnAbout));
		topicsPanel.setVisible(btn.equals(btnTopics));
		certificationPanel.setVisible(btn.equals(btnCertification));
		if (libraryPanel != null)
			libraryPanel.setVisible(btn.equals(btnLibrary));
		LoadingPopup.hide();
	}

	private FlowPanel getAboutPanel() {
		FlowPanel aboutPanel = new FlowPanel();
		aboutPanel.add(getInfosPanel());
		aboutPanel.add(getSidePanel());
		return aboutPanel;
	}

	private FlowPanel getCertificationPanel() {
		FlowPanel certificationPanel = new FlowPanel();
		certificationPanel.addStyleName("certificationPanel");
		// TODO: i18n
		certificationPanel.add(getCertificationInfo());
		certificationPanel.add(getCertificationTableHeader());
		certificationPanel.add(getCertificationTableContent());

		return certificationPanel;
	}

	private FlowPanel getLibraryPanel(LibraryFilesTO libraryFilesTO) {
		FlowPanel libraryPanel = new FlowPanel();
		libraryPanel.add(new GenericCourseLibraryView(bus, session, placeCtrl,
				libraryFilesTO));

		return libraryPanel;
	}

	private FlowPanel getCertificationInfo() {
		FlowPanel certificationInfo = new FlowPanel();
		certificationInfo.addStyleName("detailsInfo");

		Label infoTitle = new Label("Certificação");
		infoTitle.addStyleName("detailsInfoTitle");
		certificationInfo.add(infoTitle);

		Label infoText = new Label(
				/*
				 * "Confira abaixo o status dos testes e avaliações presentes neste curso. "
				 * +
				 */"Seu certificado pode ser impresso por aqui caso você tenha concluído 100% do conteúdo do curso e tenha sido aprovado na avaliação final.");
		infoText.addStyleName("detailsInfoText");
		certificationInfo.add(infoText);

		return certificationInfo;
	}

	private FlowPanel getCertificationTableContent() {
		FlowPanel certificationContentPanel = new FlowPanel();
		certificationContentPanel.addStyleName("certificationContentPanel");

		// certificationContentPanel.add(new GenericCertificationItemView(bus,
		// session, Dean.getInstance().getCourseClassTO(),
		// GenericCertificationItemView.TEST));
		certificationContentPanel.add(new GenericCertificationItemView(bus,
				session, Dean.getInstance().getCourseClassTO(),
				GenericCertificationItemView.CERTIFICATION));

		return certificationContentPanel;
	}

	private FlowPanel getCertificationTableHeader() {
		FlowPanel certificationHeaderPanel = new FlowPanel();
		certificationHeaderPanel.addStyleName("certificationHeaderPanel");

		certificationHeaderPanel.add(getHeaderButton("Item", "btnItem",
				"btnCertificationHeader"));
		certificationHeaderPanel.add(getHeaderButton("Status", "btnStatus",
				"btnCertificationHeader"));
		certificationHeaderPanel.add(getHeaderButton("Nota", "btnGrade",
				"btnCertificationHeader"));
		certificationHeaderPanel.add(getHeaderButton("Ações", "btnActions",
				"btnCertificationHeader"));

		return certificationHeaderPanel;
	}

	private Button getHeaderButton(String label, String styleName,
			String styleNameGlobal) {
		Button btn = new Button(label);
		btn.removeStyleName("btn");
		btn.addStyleName(styleNameGlobal);
		btn.addStyleName(styleName);
		btn.addStyleName("btnNotSelected");
		return btn;
	}

	private void displayTopics() {
		int i = 0;
		ExternalPage page;
		boolean enableAnchorOnNextTopicsFirstChild = true;
		if (contents != null)
			for (Content content : contents.getChildren()) {
				topicsPanel.add(new GenericTopicView(bus, session, placeCtrl,
						session, Dean.getInstance().getCourseClassTO(),
						content, i++, enableAnchorOnNextTopicsFirstChild));
				enableAnchorOnNextTopicsFirstChild = true;
				List<Content> children = new ArrayList<Content>();
				if (ContentFormat.Topic.equals(content.getFormat())) {
					children = content.getTopic().getChildren();
				}
				for (Content contentItem : children) {
					page = contentItem.getExternalPage();
					if (!page.isVisited()) {
						enableAnchorOnNextTopicsFirstChild = false;
						break;
					}
				}
			}
	}

	private void displayTitle() {
		Image titleImage = new Image(IMAGES_PATH + "details.png");
		titleImage.addStyleName("titleImage");
		titlePanel.add(titleImage);

		Label titleLabel = new Label(constants.detailsHeader() + " ");
		titleLabel.addStyleName("titleLabel");
		titlePanel.add(titleLabel);

		if (courseClassTO != null) {
			Label courseNameLabel = new Label(courseClassTO
					.getCourseVersionTO().getCourse().getTitle());
			courseNameLabel.addStyleName("courseNameLabel");
			titlePanel.add(courseNameLabel);
		}

		Label subTitleLabel = new Label(constants.detailsSubHeader() + " ");
		subTitleLabel.addStyleName("titleLabel subTitleLabel");
		titlePanel.add(subTitleLabel);

		if (courseClassTO != null) {
			Label courseClassNameLabel = new Label(courseClassTO
					.getCourseClass().getName());
			courseClassNameLabel.addStyleName("courseClassNameLabel");
			titlePanel.add(courseClassNameLabel);
		}
	}

	private FlowPanel getInfosPanel() {
		FlowPanel infoPanel = new FlowPanel();
		infoPanel.addStyleName("infoPanel");
		if (courseDetails != null)
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
		btnAbout = new Button();
		btnTopics = new Button();
		btnCertification = new Button();
		btnLibrary = new Button();
		btnGoToCourse = new Button();
		displayButton(btnAbout, constants.btnAbout(), constants.btnAboutInfo(),
				true);
		if (actoms.size() > 1) {
			displayButton(btnTopics, constants.btnTopics(),
					constants.btnTopicsInfo(), false);
		}
		// TODO: i18n
		if (isInactiveCourseClass) {
			displayButton(btnCertification, constants.btnCertification(),
					"Imprimir certificado"/* constants.btnCertificationInfo() */,
					false);
		} else if (isEnrolled && !isCancelled) {
			displayButton(btnCertification, constants.btnCertification(),
					"Imprimir certificado"/* constants.btnCertificationInfo() */,
					false);
			displayButton(
					btnLibrary,
					constants.btnLibrary(),
					"Material complementar"/* constants.btnCertificationInfo() */,
					false);
			displayButton(btnGoToCourse, "Ir para o curso", "", false);
		}
	}

	private void displayButton(Button btn, String title, String label,
			boolean isSelected) {
		btn.addStyleName("btnDetails "
				+ (isSelected ? "btnSelected" : "btnNotSelected"));

		Label btnTitle = new Label(title);
		btnTitle.addStyleName("btnTitle");
		btn.add(btnTitle);

		Label btnLabel = new Label(label);
		btnLabel.addStyleName("btnLabel");
		btn.add(btnLabel);

		btn.addStyleName("gradient");

		btn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Button btn = (Button) event.getSource();
				if (!btnGoToCourse.equals(btn)) {
					handleEvent(btn);
				} else {
					bus.fireEvent(new ShowDetailsEvent(false));
				}
			}
		});

		buttonsPanel.add(btn);
	}

	private FlowPanel getSidePanel() {
		FlowPanel sidePanel = new FlowPanel();
		sidePanel.addStyleName("sidePanel");

		if (isInactiveCourseClass || isCancelled || !isEnrolled) {
			FlowPanel warningPanel = new FlowPanel();
			warningPanel.addStyleName("notEnrolledPanel");
			String text = "";
			if (isInactiveCourseClass) {
				text = "Essa turma foi desabilitada pela instituição."
						+ "<br><br> O material desta turma está inacessível.<br>";
			} else if (isCancelled) {
				text = "Sua matrícula foi cancelada pela instituição.";
			} else if (!isEnrolled) {
				text = "Sua matrícula ainda não foi aprovada pela instituição."
						+ (Dean.getInstance().getCourseClassTO()
								.getCourseClass().isEnrollWithCPF() ? ""
								: "<br><br> Você receberá um email no momento da aprovação.<br>");
			}
			HTMLPanel panel = new HTMLPanel(text);
			warningPanel.add(panel);
			sidePanel.add(warningPanel);
		}

		sidePanel.add(getHintsPanel());

		return sidePanel;
	}

	private FlowPanel getHintsPanel() {
		FlowPanel hintsPanel = new FlowPanel();
		hintsPanel.addStyleName("hintsPanel");

		if (courseDetails != null)
			for (HintTO hintTO : courseDetails.getHints()) {
				hintsPanel
						.add(getHintPanel(hintTO.getType(), hintTO.getName()));
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

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
}
