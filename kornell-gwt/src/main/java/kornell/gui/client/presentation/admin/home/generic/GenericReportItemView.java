package kornell.gui.client.presentation.admin.home.generic;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.to.CourseClassTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.event.ProgressEvent;
import kornell.gui.client.event.ProgressEventHandler;
import kornell.gui.client.event.ShowDetailsEvent;
import kornell.gui.client.event.ShowDetailsEventHandler;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.presentation.util.LoadingPopup;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericReportItemView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericReportItemView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private EventBus bus;
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private String IMAGES_PATH = "skins/first/icons/admin/";
	private KornellSession session;
	private CourseClassTO currentCourseClass;
	private String type;
	private String name;
	private String description;
	private String status;
	private String grade;
	
	private HandlerRegistration downloadHandler;
	
	public static final String COURSE_CLASS_INFO = "courseClassInfo";
	public static final String CERTIFICATE = "certificate";
	
	@UiField
	Image certificationIcon;
	@UiField
	Label lblName;
	@UiField
	Label lblDescription;
	@UiField
	Anchor lblGenerate;
	@UiField
	Anchor lblDownload;


	public GenericReportItemView(EventBus eventBus, KornellSession session, CourseClassTO currentCourseClass,
			String type) {
		this.bus = eventBus;
		this.session = session;
		this.currentCourseClass = currentCourseClass;
		this.type = type;
		initWidget(uiBinder.createAndBindUi(this));
		display();
	}
	
	private void display() {
		if(CERTIFICATE.equals(this.type))
			displayCertificate();
		else
			displayCourseClassInfo();
	}

	private void displayCourseClassInfo() {
	  this.name = "Relatório de detalhes da turma";
		this.description = "Geração do relatório de detalhes da turma, contendo os dados da turma e informações sobre as matrículas da mesma.";
		
		certificationIcon.setUrl(IMAGES_PATH + type + ".png");
		lblName.setText(name);
		lblDescription.setText(description);
		lblGenerate.setText("Gerar");
		lblGenerate.addStyleName("cursorPointer");

		lblDownload.setText("-");
		lblDownload.removeStyleName("cursorPointer");
		lblDownload.addStyleName("anchorToLabel");
		lblDownload.setEnabled(false);
		
		lblGenerate.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				KornellNotification.show("Aguarde um instante...", AlertType.INFO, 2000);
				Window.Location.assign(session.getApiUrl() + "/report/courseClassInfo/?courseClassUUID="
						+ currentCourseClass.getCourseClass().getUUID());
			}
		});
  }

	private void displayCertificate() {
	  this.name = "Certificados de conclusão de curso";
		this.description = "Geração do certificado de todos os alunos desta turma que concluíram o curso. A geração pode levar um tempo, dependendo do tamanho da turma. Assim que ele for gerado ele estará disponível para ser baixado aqui.";
		
		certificationIcon.setUrl(IMAGES_PATH + type + ".png");
		lblName.setText(name);
		lblDescription.setText(description);
		lblGenerate.setText("Gerar");
		lblGenerate.addStyleName("cursorPointer");
		
		lblGenerate.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				displayCertificateActionCell(null);
				LoadingPopup.show();
				session.generateCourseClassCertificate(currentCourseClass.getCourseClass().getUUID(), new Callback<String>() {
					
					@Override
					public void ok(String url) {
						KornellNotification.show("Os certificados foram gerados.", AlertType.INFO, 2000);
						displayCertificateActionCell(url);
						LoadingPopup.hide();
					}
					
					@Override
					public void internalServerError() {
						KornellNotification.show("Erro na geração dos certificados. Certifique-se que existem alunos que concluíram o curso nessa turma.", AlertType.ERROR, 3000);
						displayCertificateActionCell(null);
						LoadingPopup.hide();
					}
				});
			}
		});

		session.courseClassCertificateExists(currentCourseClass.getCourseClass().getUUID(), new Callback<String>() {
			@Override
			public void ok(String str) {
				displayCertificateActionCell(str);
			}
			
			@Override
			public void internalServerError() {
				displayCertificateActionCell(null);
			}
		});
  }

	private void displayCertificateActionCell(final String url) {
	  if(url != null && !"".equals(url)) {
			lblDownload.setText("Baixar");
			lblDownload.addStyleName("cursorPointer");
			lblDownload.removeStyleName("anchorToLabel");
			downloadHandler = lblDownload.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Window.open(url,"","");
				}
			});
		} else {
			lblDownload.setText("Não disponível");
			lblDownload.removeStyleName("cursorPointer");
			lblDownload.addStyleName("anchorToLabel");
			lblDownload.setEnabled(false);
			if(downloadHandler != null){
				downloadHandler.removeHandler();
			}
		}
  }

}