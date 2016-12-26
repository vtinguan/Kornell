package kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.edit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.gwtbootstrap.client.ui.FileUpload;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dev.util.Name;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
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
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.ContentSpec;
import kornell.core.entity.CourseVersion;
import kornell.core.entity.Entity;
import kornell.core.entity.InstitutionType;
import kornell.gui.client.event.NavigationAuthorizationEvent;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView.Presenter;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.Wizard;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardElement;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlide;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItem;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItemType;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItemVideoLink;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardTopic;
import kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.WizardUtils;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.forms.formfield.KornellFormFieldWrapper;
import kornell.gui.client.util.forms.formfield.ListBoxFormField;
import kornell.gui.client.util.view.KornellNotification;
import kornell.gui.client.util.view.LoadingPopup;

public class WizardSlideItemVideoLinkView extends Composite implements IWizardView {
	interface MyUiBinder extends UiBinder<Widget, WizardSlideItemVideoLinkView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;
	private FormHelper formHelper = GWT.create(FormHelper.class);
	
	private String videoLinkTypeLabel, urlLabel;
	private KornellFormFieldWrapper videoLinkType, url;
	private List<KornellFormFieldWrapper> fields;

	@UiField	
	FlowPanel slideItemWrapper;
	@UiField
	FlowPanel slideItemFields;	

	private String changedString = "(*) ";
	
	private WizardSlideItemView wizardSlideItemView;
	
	private WizardSlideItem wizardSlideItem;
	private WizardSlideItemVideoLink wizardSlideItemVideoLink;
	
	private KeyUpHandler refreshFormKeyUpHandler;

	private Presenter presenter;	
	
	public enum VideoLinkType {
		YOUTUBE,
		VIMEO
	} 

	public WizardSlideItemVideoLinkView(WizardSlideItem wizardSlideItem, WizardSlideItemView wizardSlideItemView, Presenter presenter) {
		this.presenter = presenter;
		this.wizardSlideItem = wizardSlideItem;
		this.wizardSlideItemView = wizardSlideItemView;
		String extra = wizardSlideItem.getExtra();
		extra = extra == null ? "{}" : extra;
		this.wizardSlideItemVideoLink = AutoBeanCodex.decode(WizardUtils.WIZARD_FACTORY, WizardSlideItemVideoLink.class, extra).as();
		initWidget(uiBinder.createAndBindUi(this));
		init();
	}

	public void init() {
		fields = new ArrayList<KornellFormFieldWrapper>();
		slideItemFields.clear();	
		
		refreshFormKeyUpHandler = new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				wizardSlideItemView.refreshForm();
			}
		};

		final ListBox videoLinkTypes = new ListBox();
		videoLinkTypes.addItem("YouTube", VideoLinkType.YOUTUBE.toString());
		videoLinkTypes.addItem("Vimeo", VideoLinkType.VIMEO.toString());
		videoLinkTypes.setSelectedValue(wizardSlideItemVideoLink.getVideoLinkType().toString());
		videoLinkTypeLabel = "Tipo";
		videoLinkType = new KornellFormFieldWrapper(videoLinkTypeLabel, new ListBoxFormField(videoLinkTypes), true);
		((ListBox)videoLinkType.getFieldWidget()).addKeyUpHandler(refreshFormKeyUpHandler);
		fields.add(videoLinkType);
		slideItemFields.add(videoLinkType);	

		urlLabel = "URL do vídeo";
		url = new KornellFormFieldWrapper(urlLabel, formHelper.createTextBoxFormField(wizardSlideItemVideoLink.getURL()), true);
		((TextBox)url.getFieldWidget()).addKeyUpHandler(refreshFormKeyUpHandler);
		fields.add(url);
		slideItemFields.add(url);		
	}

	@Override
	public void resetFormToOriginalValues(){	
		((ListBox)videoLinkType.getFieldWidget()).setSelectedValue(wizardSlideItemVideoLink.getVideoLinkType().toString());
		((TextBox)url.getFieldWidget()).setText(wizardSlideItemVideoLink.getURL());

		presenter.valueChanged(wizardSlideItem, false);
		refreshForm();
	}

	@Override
	public boolean refreshForm(){
		boolean valueHasChanged = refreshFormElementLabel(videoLinkType, videoLinkTypeLabel, wizardSlideItemVideoLink.getVideoLinkType()) || 
				refreshFormElementLabel(url, urlLabel, wizardSlideItemVideoLink.getURL());
		presenter.valueChanged(wizardSlideItem, valueHasChanged);
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

		if (!formHelper.isLengthValid(videoLinkType.getFieldPersistText(), 2, 100)) {
			videoLinkType.setError("Insira o tipo");
		}
		if (!formHelper.isLengthValid(url.getFieldPersistText(), 2, 100)) {
			url.setError("Insira a URL");
		}

		return !formHelper.checkErrors(fields);
	}

	@Override
	public void updateWizard() {
		wizardSlideItemVideoLink.setVideoLinkType(videoLinkType.getFieldPersistText());
		wizardSlideItemVideoLink.setURL(url.getFieldPersistText());

		wizardSlideItem.setExtra(AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(wizardSlideItemVideoLink)).getPayload().toString());
		presenter.valueChanged(wizardSlideItemVideoLink, false);	
		refreshForm();	
	}

	public WizardSlideItem getWizardSlideItem() {
		return wizardSlideItem;
	}

	public String getUrl() {
		return url.getFieldPersistText();
	}
}