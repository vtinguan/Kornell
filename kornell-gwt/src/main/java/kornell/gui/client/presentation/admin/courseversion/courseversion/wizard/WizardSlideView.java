package kornell.gui.client.presentation.admin.courseversion.courseversion.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.FileUpload;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.Icon;
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
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView.Presenter;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.Wizard;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardElement;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardFactory;
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

public class WizardSlideView extends Composite implements IWizardView {
	interface MyUiBinder extends UiBinder<Widget, WizardSlideView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	public static final WizardFactory WIZARD_FACTORY = GWT.create(WizardFactory.class);

	private EventBus bus;
	private KornellSession session;
	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;
	private FormHelper formHelper = GWT.create(FormHelper.class);
	private String PLAIN_CLASS = "plainDiscreteTextColor";
	private KornellFormFieldWrapper title;
	private List<KornellFormFieldWrapper> fields;

	@UiField	
	FlowPanel slidePanel;
	@UiField
	ScrollPanel slideItemsScroll;
	@UiField
	FlowPanel slidePanelItems;
	@UiField
	Form form;
	@UiField
	FlowPanel slideFields;
	@UiField
	Button btnSave;
	@UiField
	Button btnDiscard;
	@UiField
	Button btnNewTextItem;
	@UiField
	Button btnNewVideoLinkItem;
	
	private Presenter presenter;
	

	private String titleLabel;
	private String changedString = "(*) ";

	private ChangeHandler refreshFormChangeHandler;

	public WizardSlideView() {
		initWidget(uiBinder.createAndBindUi(this));
		refreshFormChangeHandler = new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				refreshForm();
			}
		};
		btnSave.add(createIcon("fa-floppy-o"));
		btnDiscard.add(createIcon("fa-times"));
		btnNewTextItem.add(createIcon(WizardUtils.getClasForWizardSlideItemViewIcon(WizardSlideItemType.TEXT)));
		btnNewVideoLinkItem.add(createIcon(WizardUtils.getClasForWizardSlideItemViewIcon(WizardSlideItemType.VIDEO_LINK)));
	} 
	
	@UiHandler("btnNewTextItem")
	void doNewTextItem(ClickEvent e) {
		WizardSlideItem wizardSlideItem = WizardUtils.newWizardSlideItem();
		wizardSlideItem.setWizardSlideItemType(WizardSlideItemType.TEXT);
		wizardSlideItemCreated(wizardSlideItem);
	}
	
	@UiHandler("btnNewVideoLinkItem")
	void doNewVideoLinkItem(ClickEvent e) {
		WizardSlideItem wizardSlideItem = WizardUtils.newWizardSlideItem();
		wizardSlideItem.setWizardSlideItemType(WizardSlideItemType.VIDEO_LINK);
		wizardSlideItemCreated(wizardSlideItem);
	}

	private void wizardSlideItemCreated(WizardSlideItem wizardSlideItem) {
		WizardSlideItemView wizardSlideItemView = new WizardSlideItemView(wizardSlideItem, presenter);
		wizardSlideItemView.refreshForm();
		
		slidePanelItems.add(wizardSlideItemView);
		
		WizardSlide wizardSlide = (WizardSlide) presenter.getSelectedWizardElement();
		wizardSlide.getWizardSlideItems().add(wizardSlideItem);
		
		refreshForm();

		slideItemsScroll.scrollToBottom();
	}
	
	private Icon createIcon(String iconClass){
		Icon icon = new Icon();
		icon.addStyleName("fa " + iconClass);
		return icon;
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public void displaySlidePanel(boolean display) {
		slidePanel.setVisible(display);
	}

	public void updateSlidePanel() {
		WizardElement selectedWizardElement = presenter.getSelectedWizardElement();
		this.fields = new ArrayList<KornellFormFieldWrapper>();
		slideFields.clear();	
		slidePanelItems.clear();

		titleLabel = "Título do Slide";
		title = new KornellFormFieldWrapper(titleLabel, formHelper.createTextBoxFormField(selectedWizardElement.getTitle()), true);
		((TextBox)title.getFieldWidget()).addChangeHandler(refreshFormChangeHandler);
		fields.add(title);
		slideFields.add(title);		

		if(selectedWizardElement instanceof WizardSlide){
			WizardSlide wizardSlide = (WizardSlide) selectedWizardElement;
			WizardSlideItemView wizardSlideItemView; 
			for (final WizardSlideItem wizardSlideItem : wizardSlide.getWizardSlideItems()) {
				wizardSlideItemView = new WizardSlideItemView(wizardSlideItem, presenter);
				slidePanelItems.add(wizardSlideItemView);
			} 
		}
	}

	@Override
	public void resetFormToOriginalValues(){
		WizardElement selectedWizardElement = presenter.getSelectedWizardElement();
		
		((TextBox)title.getFieldWidget()).setText(selectedWizardElement.getTitle());
		WizardSlideItemView wizardSlideItemView;
		Widget widget;
		for(int i = 0; i < slidePanelItems.getWidgetCount(); i++){
			widget = slidePanelItems.getWidget(i);
			wizardSlideItemView = (WizardSlideItemView)widget;
			
			if(wizardSlideItemView.getWizardSlideItem().getUUID() == null){
				slidePanelItems.remove(widget);
				i--;
				//remove from model
				for(WizardSlideItem wizardSlideItem : ((WizardSlide)selectedWizardElement).getWizardSlideItems()){
					if(wizardSlideItem.equals(wizardSlideItemView.getWizardSlideItem())){
						((WizardSlide)selectedWizardElement).getWizardSlideItems().remove(wizardSlideItem);
						break;
					}
				}
				continue;
			}
			
			wizardSlideItemView.resetFormToOriginalValues();
		}
		
		presenter.valueChanged(selectedWizardElement, false);
		refreshForm();
	}
	
	@Override
	public boolean refreshForm(){
		WizardElement selectedWizardElement = presenter.getSelectedWizardElement();
		
		boolean valueHasChanged = updateTitleFormElement(selectedWizardElement.getTitle());
		
		validateFields();
		
		return valueHasChanged;
	}
	 
	private boolean updateTitleFormElement(String originalValue){
		boolean valueHasChanged = !title.getFieldPersistText().equals(originalValue);
		presenter.valueChanged(valueHasChanged);
		title.setFieldLabelText((valueHasChanged ? changedString  : "") + titleLabel);
		return valueHasChanged;
	}
	
	@UiHandler("btnSave")
	void doOK(ClickEvent e) {
		if(!WizardUtils.wizardElementHasValueChanged(presenter.getSelectedWizardElement())){
			KornellNotification.show("Alterações salvas com sucesso.");
			return;
		}

		if (validateFields()) {
			WizardElement selectedWizardElement = presenter.getSelectedWizardElement();
			selectedWizardElement.setTitle(title.getFieldPersistText());
			
			for(Widget wizardSlideItemView : slidePanelItems){
				((WizardSlideItemView)wizardSlideItemView).updateWizard();
			}

			//@TODO CALLBACK
			presenter.valueChanged(false);
			
			for(Widget wizardSlideItemView : slidePanelItems){
				((WizardSlideItemView)wizardSlideItemView).getWizardSlideItem().setValueChanged(false);
				((WizardSlideItemView)wizardSlideItemView).getWizardSlideItem().setUUID("new");
				((WizardSlideItemView)wizardSlideItemView).refreshForm();
			}
			
			KornellNotification.show("Alterações salvas com sucesso.");
			refreshForm();
			//@TODO CALLBACK

		} else {
			KornellNotification.show("Existem erros nos dados.", AlertType.ERROR);
		}
	}
	
	@UiHandler("btnDiscard")
	void doDiscard(ClickEvent e) {
		if(!WizardUtils.wizardElementHasValueChanged(presenter.getSelectedWizardElement())){
			return;
		}
		
		resetFormToOriginalValues();
	}

	@Override
	public boolean validateFields() {	
		formHelper.clearErrors(fields);
		
		boolean errorsFound = false;
		
		if (!formHelper.isLengthValid(title.getFieldPersistText(), 2, 100)) {
			title.setError("Insira o título");
		}
		
		for(Widget wizardSlideItemView : slidePanelItems){
			errorsFound = errorsFound || !((WizardSlideItemView)wizardSlideItemView).validateFields();
		}
		
		errorsFound = errorsFound || formHelper.checkErrors(fields);
		return !errorsFound;
	}

	@Override
	public void updateWizard() {
		// TODO Auto-generated method stub
		
	}
}