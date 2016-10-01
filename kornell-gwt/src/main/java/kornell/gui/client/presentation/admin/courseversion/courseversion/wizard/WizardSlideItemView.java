package kornell.gui.client.presentation.admin.courseversion.courseversion.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.gwtbootstrap.client.ui.FileUpload;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dev.util.Name;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.ContentSpec;
import kornell.core.entity.CourseVersion;
import kornell.core.entity.Entity;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView.Presenter;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.Wizard;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardElement;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardMock;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlide;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItem;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItemType;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardTopic;
import kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.WizardUtils;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.forms.formfield.KornellFormFieldWrapper;
import kornell.gui.client.util.view.KornellNotification;
import kornell.gui.client.util.view.LoadingPopup;

public class WizardSlideItemView extends Composite implements IWizardView {
	interface MyUiBinder extends UiBinder<Widget, WizardSlideItemView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;
	private FormHelper formHelper = GWT.create(FormHelper.class);
	
	private String titleLabel, textLabel;
	private KornellFormFieldWrapper title, text;
	private List<KornellFormFieldWrapper> fields;
	private IWizardView extendedItemView;
	
	@UiField	
	FlowPanel slideItemWrapper;
	@UiField
	Icon slideItemIcon;
	@UiField
	Label slideItemLabel;
	@UiField
	Form form;
	@UiField
	FlowPanel slideItemFields;	

	private String changedString = "(*) ";
	
	private WizardSlideItem wizardSlideItem;

	private ChangeHandler refreshFormChangeHandler;

	private Presenter presenter;

	public WizardSlideItemView(WizardSlideItem wizardSlideItem, Presenter presenter) {
		this.presenter = presenter;
		this.wizardSlideItem = wizardSlideItem;
		initWidget(uiBinder.createAndBindUi(this));
		init();
	}

	public void init() {
		slideItemIcon.addStyleName(WizardUtils.getClasForWizardSlideItemViewIcon(wizardSlideItem.getWizardSlideItemType()));
		slideItemLabel.setText(wizardSlideItem.getTitle());
		fields = new ArrayList<KornellFormFieldWrapper>();
		slideItemFields.clear();	
		
		refreshFormChangeHandler = new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				refreshForm();
			}
		};

		titleLabel = "Título do Item";
		title = new KornellFormFieldWrapper(titleLabel, formHelper.createTextBoxFormField(wizardSlideItem.getTitle()), true);
		((TextBox)title.getFieldWidget()).addChangeHandler(refreshFormChangeHandler);
		fields.add(title);
		slideItemFields.add(title);	

		textLabel = "Texto";
		text = new KornellFormFieldWrapper(textLabel, formHelper.createTextAreaFormField(wizardSlideItem.getText(), 5), true);
		((TextArea)text.getFieldWidget()).addChangeHandler(refreshFormChangeHandler);
		text.addStyleName("heightAuto marginBottom25");
		fields.add(text);
		slideItemFields.add(text);	
		
		switch (wizardSlideItem.getWizardSlideItemType()) {
		case IMAGE:
			break;
		case QUIZ:
			break;
		case TEXT:
			break;
		case VIDEO_LINK:
			extendedItemView = new WizardSlideItemVideoLinkView(wizardSlideItem, this, presenter);
			slideItemFields.add((WizardSlideItemVideoLinkView)extendedItemView);	
			break;
		default:
			break;
		}
	}

	@Override
	public void resetFormToOriginalValues(){	
		((TextBox)title.getFieldWidget()).setText(wizardSlideItem.getTitle());
		((TextArea)text.getFieldWidget()).setText(wizardSlideItem.getText());
		
		if(extendedItemView != null){
			extendedItemView.resetFormToOriginalValues();
		}

		presenter.valueChanged(wizardSlideItem, false);
		refreshForm();
	}

	@Override
	public boolean refreshForm(){
		boolean valueHasChanged = refreshFormElementLabel(title, titleLabel, wizardSlideItem.getTitle()) || 
				refreshFormElementLabel(text, textLabel, wizardSlideItem.getText()) ||
				wizardSlideItem.getUUID() == null;
		if(extendedItemView != null){
			valueHasChanged = valueHasChanged || extendedItemView.refreshForm();
		}
		
		presenter.valueChanged(wizardSlideItem, valueHasChanged);
		slideItemLabel.setText((valueHasChanged ? changedString  : "") + wizardSlideItem.getTitle());
		
		validateFields();
		
		return valueHasChanged;
	}
	 
	private boolean refreshFormElementLabel(KornellFormFieldWrapper kornellFormFieldWrapper, String label, String originalValue){
		boolean valueHasChanged = !kornellFormFieldWrapper.getFieldPersistText().equals(originalValue);
		kornellFormFieldWrapper.setFieldLabelText((valueHasChanged ? changedString  : "") + label);
		return valueHasChanged;
	}

	@Override
	public boolean validateFields() {		
		formHelper.clearErrors(fields);

		if (WizardSlideItemType.TEXT.equals(wizardSlideItem.getWizardSlideItemType()) &&
				!formHelper.isLengthValid(title.getFieldPersistText(), 2, 100)) {
			title.setError("Insira o título");
		}
		if (WizardSlideItemType.TEXT.equals(wizardSlideItem.getWizardSlideItemType()) &&
				!formHelper.isLengthValid(text.getFieldPersistText(), 2, 100)) {
			text.setError("Insira o texto");
		}
		
		boolean extendedItemViewValidated = true;
		if(extendedItemView != null){
			extendedItemViewValidated = extendedItemView.validateFields();
		}

		return extendedItemViewValidated && !formHelper.checkErrors(fields);
	}

	@Override
	public void updateWizard() {
		wizardSlideItem.setTitle(title.getFieldPersistText());
		wizardSlideItem.setText(text.getFieldPersistText());
		
		if(extendedItemView != null){
			extendedItemView.updateWizard();
		}
		
		presenter.valueChanged(wizardSlideItem, false);	
		refreshForm();	
	}

	public WizardSlideItem getWizardSlideItem() {
		return wizardSlideItem;
	}
}