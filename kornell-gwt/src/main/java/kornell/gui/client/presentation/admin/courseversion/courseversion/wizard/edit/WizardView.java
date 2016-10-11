package kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.edit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.gwtbootstrap.client.ui.FileUpload;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
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
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView.Presenter;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.Wizard;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardElement;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlide;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItem;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItemType;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardTopic;
import kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.WizardUtils;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.forms.formfield.KornellFormFieldWrapper;
import kornell.gui.client.util.view.KornellNotification;
import kornell.gui.client.util.view.LoadingPopup;

public class WizardView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, WizardView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private EventBus bus;
	private KornellSession session;
	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;
	private FormHelper formHelper = GWT.create(FormHelper.class);
	private List<Label> sideItems;
	private HashMap<String, Label> sidePanelItemsMap;
	private static String HIGHLIGHT_CLASS = "highlightText";
	private String PLAIN_CLASS = "plainDiscreteTextColor";
	private KornellFormFieldWrapper name;
	private List<KornellFormFieldWrapper> fields;

	@UiField
	FlowPanel wizardPanel;
	@UiField	
	ScrollPanel slideItemsScroll;
	@UiField	
	FlowPanel sidePanel;
	@UiField
	WizardSlideView wizardSlideView;


	private Wizard wizard;
	private CourseVersion courseVersion;
	private Presenter presenter;
	
	private String changedString = "(*) ";

	public WizardView(final KornellSession session, EventBus bus) {
		this.session = session;
		this.bus = bus;
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void init(CourseVersion courseVersion, Wizard wizard, Presenter presenter) {
		this.courseVersion = courseVersion;
		this.wizard = wizard;
		this.presenter = presenter;
		updateSidePanel();
		wizardSlideView.setPresenter(presenter);
		wizardSlideView.updateSlidePanel();
	}

	public void updateSidePanel() {
		sidePanel.clear();
		sideItems = new ArrayList<Label>();
		sidePanelItemsMap = new HashMap<String, Label>();

		WizardElement selectedWizardElement = presenter.getSelectedWizardElement();
		if(selectedWizardElement == null){
			return;
		}
		for (final WizardTopic wizardTopic : wizard.getWizardTopics()) {
			createSidePanelItem(wizard, selectedWizardElement, null, wizardTopic);
			for (final WizardSlide wizardSlide : wizardTopic.getWizardSlides()) {
				createSidePanelItem(wizard, selectedWizardElement, wizardTopic, wizardSlide);				
			}
		}

		if(selectedWizardElement != null){
			sidePanelItemsMap.get(selectedWizardElement.getUUID()).addStyleName("selected");
		}
	}

	private void createSidePanelItem(Wizard wizard, WizardElement selectedWizardElement, final WizardTopic parentWizardElement,
			WizardElement wizardElement) {
		final Label label = new Label();
		label.addStyleName("sidePanelItem");
		label.addClickHandler(new ClickHandler() {
			boolean enableClick = true;
			@Override
			public void onClick(ClickEvent event) {
				if(!enableClick || 
						(selectedWizardElement != null && 
								selectedWizardElement.getUUID().equals(wizardElement.getUUID()))){
					return;
				}
				if(WizardUtils.wizardElementHasValueChanged(selectedWizardElement)){
					KornellNotification.show("Salve ou descarte as alterações antes de trocar de slide.", AlertType.WARNING);
					return;
				}
				wizardSlideView.displaySlidePanel(false);	
				LoadingPopup.show();
				Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
					@Override
					public void execute() {
						for (Label lbl : sideItems) {
							lbl.removeStyleName("selected");
						}
						label.addStyleName("selected");
						setLabelContent(wizardElement, label);
						presenter.wizardElementClicked(wizardElement);
					}
				});
			}
		});
		if(parentWizardElement != null){
			label.addStyleName("marginLeft25");
		}
		setLabelContent(wizardElement, label);
		sidePanel.add(label);
		sideItems.add(label);
	}

	private void setLabelContent(WizardElement wizardElement, final Label label) {
		String valueChanged = WizardUtils.wizardElementHasValueChanged(wizardElement) ? changedString : "";
		String title = valueChanged + 
				(wizardElement.getParentOrder() == null ? 
						"Tópico " : 
						("Slide " + wizardElement.getParentOrder() + ".")) +
				(wizardElement.getOrder()+1); 
		String labelText = span(title, HIGHLIGHT_CLASS) + separator(true) + span(wizardElement.getTitle(), PLAIN_CLASS);
		sidePanelItemsMap.put(wizardElement.getUUID(), label);
		label.getElement().setInnerHTML(labelText);
	}

	private String separator(boolean lineBreak) {
		return separator(lineBreak, false);
	}

	private String separator(boolean lineBreak, boolean dash) {
		return lineBreak ? "<br>" : (dash ? "&nbsp;&nbsp;-&nbsp;&nbsp;" : "&nbsp;&nbsp;&nbsp;");
	}

	private String span(String str, String className) {
		return "<span class=\""+className+"\">"+str+"</span>";
	}

	public void toggleViewMode(boolean isViewModeOn) {
		this.slideItemsScroll.setVisible(!isViewModeOn);
	}

	public void displaySlidePanel(boolean display) {
		this.wizardSlideView.displaySlidePanel(display);		
	}

	public void updateSlidePanel() {
		this.wizardSlideView.updateSlidePanel();		
	}

	public void refreshSlidePanel() {
		this.wizardSlideView.refreshForm();		
	}
}