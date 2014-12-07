package kornell.gui.client.presentation.admin.course.course.generic;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.ContentSpec;
import kornell.core.entity.Course;
import kornell.core.entity.EntityFactory;
import kornell.core.to.CoursesTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.admin.course.course.AdminCoursePlace;
import kornell.gui.client.presentation.admin.course.course.AdminCourseView;
import kornell.gui.client.presentation.admin.course.courses.AdminCoursesPlace;
import kornell.gui.client.presentation.admin.institution.AdminInstitutionPlace;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.presentation.util.LoadingPopup;
import kornell.gui.client.util.view.formfield.KornellFormFieldWrapper;
import kornell.gui.client.util.view.formfield.ListBoxFormField;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericAdminCourseView extends Composite implements AdminCourseView {

	interface MyUiBinder extends UiBinder<Widget, GenericAdminCourseView> {
	}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	public static final EntityFactory entityFactory = GWT.create(EntityFactory.class);
	private static final String MODAL_DELETE = "delete";
	private static final String MODAL_DEACTIVATE = "deactivate";
	private static final String MODAL_PUBLIC = "public";
	private static final String MODAL_OVERRIDE_ENROLLMENTS = "overrideEnrollments";
	private static final String MODAL_INVISIBLE = "invisible";

	private KornellSession session;
	private PlaceController placeCtrl;
	private KornellConstants constants = GWT.create(KornellConstants.class);
	private FormHelper formHelper = GWT.create(FormHelper.class);
	private boolean isCreationMode, isPlatformAdmin;
	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;

	private Presenter presenter;

	@UiField
	HTMLPanel titleEdit;
	@UiField
	Form form;
	@UiField
	FlowPanel courseFields;
	@UiField
	Button btnOK;
	@UiField
	Button btnCancel;

	@UiField
	Modal confirmModal;
	@UiField
	Label confirmText;
	@UiField
	Button btnModalOK;
	@UiField
	Button btnModalCancel;

	private Course course;

	private KornellFormFieldWrapper code, title, description;
	
	private FileUpload fileUpload;
	private List<KornellFormFieldWrapper> fields;
	private String modalMode;
	private ListBox institutionRegistrationPrefixes;
	private String courseUUID;
	
	public GenericAdminCourseView(final KornellSession session, EventBus bus, PlaceController placeCtrl) {
		this.session = session;
		this.placeCtrl = placeCtrl;
		this.isPlatformAdmin = session.isPlatformAdmin();
		initWidget(uiBinder.createAndBindUi(this));

		// i18n
		btnOK.setText("Salvar".toUpperCase());
		btnCancel.setText("Cancelar".toUpperCase());	

		btnModalOK.setText("OK".toUpperCase());
		btnModalCancel.setText("Cancelar".toUpperCase());
		
		bus.addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						if(event.getNewPlace() instanceof AdminCoursePlace)
							init();
					}
				});
	}
	
	@Override
	public void init(){

		if(placeCtrl.getWhere() instanceof AdminCoursePlace && ((AdminCoursePlace)placeCtrl.getWhere()).getCourseUUID() != null){
			this.courseUUID = ((AdminCoursePlace)placeCtrl.getWhere()).getCourseUUID();
			isCreationMode = false;
		} else {
			isCreationMode = true;
		}
		
		if(isCreationMode){
			course = presenter.getNewCourse();
			initData();	
		} else {	
			session.course(courseUUID).get(new Callback<Course>() {
				@Override
				public void ok(Course to) {
					course = to;
					initData();
				}
			});
		}
	}

	public void initData() {
		courseFields.setVisible(false);
		this.fields = new ArrayList<KornellFormFieldWrapper>();

		courseFields.clear();
		
		btnOK.setVisible(isPlatformAdmin|| isCreationMode);
		btnCancel.setVisible(isPlatformAdmin);		

		code = new KornellFormFieldWrapper("Código", formHelper.createTextBoxFormField(course.getCode()), isPlatformAdmin);
		fields.add(code);
		courseFields.add(code);
		
		title = new KornellFormFieldWrapper("Nome", formHelper.createTextBoxFormField(course.getTitle()), isPlatformAdmin);
		fields.add(title);
		courseFields.add(title);

		description = new KornellFormFieldWrapper("Descrição", formHelper.createTextBoxFormField(course.getDescription()), isPlatformAdmin);
		fields.add(description);
		courseFields.add(description);
		
		courseFields.add(formHelper.getImageSeparator());

		courseFields.setVisible(true);
	}
	
	private boolean validateFields() {		
		if (!formHelper.isLengthValid(code.getFieldPersistText(), 2, 5)) {
			code.setError("O código deve ter entre 2 e 5 caracteres");
		}		
		if (!formHelper.isLengthValid(title.getFieldPersistText(), 2, 100)) {
			title.setError("Insira o título do curso");
		}
		if (!formHelper.isLengthValid(description.getFieldPersistText(), 2, 200)) {
			description.setError("Insira a descrição");
		}
		
		return !formHelper.checkErrors(fields);
	}

	@UiHandler("btnOK")
	void doOK(ClickEvent e) {
		formHelper.clearErrors(fields);
		if (isPlatformAdmin && validateFields()) {
			LoadingPopup.show();
			Course course = getCourseInfoFromForm();
			presenter.upsertCourse(course);
		}
	}

	private Course getCourseInfoFromForm() {
		course.setCode(code.getFieldPersistText());
		course.setTitle(title.getFieldPersistText());
		course.setDescription(description.getFieldPersistText());
		course.setInstitutionUUID(Dean.getInstance().getInstitution().getUUID());
		return course;
	}

	@UiHandler("btnCancel")
	void doCancel(ClickEvent e) {
		this.placeCtrl.goTo(new AdminCoursesPlace());
	}
	
	@Override
  public void setPresenter(Presenter presenter) {
	  this.presenter = presenter;
  }

}