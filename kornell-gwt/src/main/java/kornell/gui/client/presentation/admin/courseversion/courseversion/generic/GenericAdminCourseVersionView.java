package kornell.gui.client.presentation.admin.courseversion.courseversion.generic;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.ContentSpec;
import kornell.core.entity.Course;
import kornell.core.entity.CourseVersion;
import kornell.core.entity.EntityFactory;
import kornell.core.entity.RegistrationEnrollmentType;
import kornell.core.to.CourseVersionTO;
import kornell.core.to.CoursesTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionPlace;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionView;
import kornell.gui.client.presentation.admin.courseversion.courseversions.AdminCourseVersionsPlace;
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

public class GenericAdminCourseVersionView extends Composite implements AdminCourseVersionView {

	interface MyUiBinder extends UiBinder<Widget, GenericAdminCourseVersionView> {
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
	FlowPanel courseVersionFields;
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

	private CourseVersion courseVersion;

	private KornellFormFieldWrapper name, course, distributionPrefix, contentSpec, disabled;
	
	private FileUpload fileUpload;
	private List<KornellFormFieldWrapper> fields;
	private String modalMode;
	private ListBox institutionRegistrationPrefixes;
	private String courseVersionUUID;
	
	public GenericAdminCourseVersionView(final KornellSession session, EventBus bus, PlaceController placeCtrl) {
		this.session = session;
		this.placeCtrl = placeCtrl;
		this.isPlatformAdmin = session.isPlatformAdmin();
		initWidget(uiBinder.createAndBindUi(this));

		if(placeCtrl.getWhere() instanceof AdminCourseVersionPlace && ((AdminCourseVersionPlace)placeCtrl.getWhere()).getCourseVersionUUID() != null){
			this.courseVersionUUID = ((AdminCourseVersionPlace)placeCtrl.getWhere()).getCourseVersionUUID();
			isCreationMode = false;
		} else {
			isCreationMode = true;
		}

		// i18n
		btnOK.setText("Salvar".toUpperCase());
		btnCancel.setText("Cancelar".toUpperCase());	

		btnModalOK.setText("OK".toUpperCase());
		btnModalCancel.setText("Cancelar".toUpperCase());
		
		bus.addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						if(event.getNewPlace() instanceof AdminCourseVersionPlace)
							init();
					}
				});
	}
	
	@Override
	public void init(){
		if(isCreationMode){
			courseVersion = presenter.getNewCourseVersion();
			initData();	
		} else {	
			session.courseVersion(courseVersionUUID).get(new Callback<CourseVersionTO>() {
				@Override
				public void ok(CourseVersionTO to) {
					courseVersion = to.getCourseVersion();
					initData();
				}
			});
		}
	}

	public void initData() {
		courseVersionFields.setVisible(false);
		this.fields = new ArrayList<KornellFormFieldWrapper>();

		courseVersionFields.clear();
		
		btnOK.setVisible(isPlatformAdmin|| isCreationMode);
		btnCancel.setVisible(isPlatformAdmin);		

		session.courses().get(new Callback<CoursesTO>() {
				@Override
				public void ok(CoursesTO to) {
					createCoursesField(to);
					if(!isCreationMode)
						((ListBox)course.getFieldWidget()).setSelectedValue(courseVersion.getCourseUUID());
				}
			});

		name = new KornellFormFieldWrapper("Nome", formHelper.createTextBoxFormField(courseVersion.getName()), isPlatformAdmin);
		fields.add(name);
		courseVersionFields.add(name);

		distributionPrefix = new KornellFormFieldWrapper("Prefixo de Distribuição", formHelper.createTextBoxFormField(courseVersion.getDistributionPrefix()), isPlatformAdmin);
		fields.add(distributionPrefix);
		courseVersionFields.add(distributionPrefix);

		String contentSpecAttr = courseVersion.getContentSpec() != null ? courseVersion.getContentSpec().toString() : "";
		contentSpec = new KornellFormFieldWrapper("Tipo", formHelper.createTextBoxFormField(contentSpecAttr), isPlatformAdmin);
		fields.add(contentSpec);
		courseVersionFields.add(contentSpec);
		
		final ListBox contentSpecTypes = new ListBox();
		contentSpecTypes.addItem("KNL", ContentSpec.KNL.toString());
		contentSpecTypes.addItem("SCORM12", ContentSpec.SCORM12.toString());
		if (!isCreationMode) {
			contentSpecTypes.setSelectedValue(courseVersion.getContentSpec().toString());
		}
		contentSpec = new KornellFormFieldWrapper("Tipo", new ListBoxFormField(contentSpecTypes), isPlatformAdmin);
		fields.add(contentSpec);
		courseVersionFields.add(contentSpec);

		disabled = new KornellFormFieldWrapper("Desabilitar?", formHelper.createCheckBoxFormField(courseVersion.isDisabled()), isPlatformAdmin);
		fields.add(disabled);
		courseVersionFields.add(disabled);
		((CheckBox)disabled.getFieldWidget()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()){
				}
			}
		});
		
		courseVersionFields.add(formHelper.getImageSeparator());

		courseVersionFields.setVisible(true);
	}

	private void createCoursesField(CoursesTO to) {
		final ListBox courses = new ListBox();
		if(to != null){
			for (Course course : to.getCourses()) {
				courses.addItem(course.getTitle(), course.getUUID());
			}
		}/* else {
			courses.addItem(courseVersion.getCourse().getTitle(), courseVersion.getCourse().getUUID());
		}*/
		courses.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
			}
		});
		if (!isCreationMode) {
			courses.setSelectedValue(courseVersion.getCourseUUID());
		}
		course = new KornellFormFieldWrapper("Curso", new ListBoxFormField(courses), isPlatformAdmin);
				
		
		fields.add(course);
		courses.setSelectedIndex(0);
		courseVersionFields.insert(course, 0);
	}
	
	private boolean validateFields() {		
		if (!formHelper.isLengthValid(name.getFieldPersistText(), 2, 100)) {
			name.setError("Insira o nome da versão");
		}
		if (!formHelper.isListBoxSelected((ListBox)course.getFieldWidget())) {
			course.setError("Escolha o curso");
		}
		if (!formHelper.isLengthValid(distributionPrefix.getFieldPersistText(), 2, 200)) {
			distributionPrefix.setError("Insira o prefixo de distribuição");
		}
		if (!formHelper.isLengthValid(contentSpec.getFieldPersistText(), 2, 20)) {
			contentSpec.setError("Insira o tipo");
		} else {
			try {
				ContentSpec.valueOf(contentSpec.getFieldPersistText());
	    } catch (Exception e) {
				contentSpec.setError("Tipo inválido.");
	    }
		}
		
		return !formHelper.checkErrors(fields);
	}

	@UiHandler("btnOK")
	void doOK(ClickEvent e) {
		formHelper.clearErrors(fields);
		if (isPlatformAdmin && validateFields()) {
			LoadingPopup.show();
			CourseVersion courseVersion = getCourseVersionInfoFromForm();
			presenter.upsertCourseVersion(courseVersion);
		}
	}

	private CourseVersion getCourseVersionInfoFromForm() {
		CourseVersion version = courseVersion;
		version.setName(name.getFieldPersistText());
		version.setCourseUUID(course.getFieldPersistText());
		version.setDistributionPrefix(distributionPrefix.getFieldPersistText());
		version.setContentSpec(ContentSpec.valueOf(contentSpec.getFieldPersistText()));
		version.setDisabled(disabled.getFieldPersistText().equals("true"));
		return version;
	}

	@UiHandler("btnCancel")
	void doCancel(ClickEvent e) {
		this.placeCtrl.goTo(new AdminCourseVersionsPlace());
	}
	
	@Override
  public void setPresenter(Presenter presenter) {
	  this.presenter = presenter;
  }

}