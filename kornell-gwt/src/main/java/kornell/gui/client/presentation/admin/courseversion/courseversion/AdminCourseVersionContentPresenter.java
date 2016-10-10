package kornell.gui.client.presentation.admin.courseversion.courseversion;

import java.util.logging.Logger;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.core.entity.ContentSpec;
import kornell.core.entity.CourseVersion;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.Wizard;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardElement;
import kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.WizardMock;
import kornell.gui.client.util.view.LoadingPopup;

public class AdminCourseVersionContentPresenter implements AdminCourseVersionContentView.Presenter {
	Logger logger = Logger.getLogger(AdminCourseVersionContentPresenter.class.getName());
	private AdminCourseVersionContentView view;
	private KornellSession session;
	private PlaceController placeController;
	private EventBus bus;
	Place defaultPlace;
	private ViewFactory viewFactory;
	private CourseVersion courseVersion;

	private Wizard wizard;
	private WizardElement selectedWizardElement;

	public AdminCourseVersionContentPresenter(KornellSession session, PlaceController placeController, EventBus bus,
			Place defaultPlace, ViewFactory viewFactory) {
		this.session = session;
		this.placeController = placeController;
		this.bus = bus;
		this.defaultPlace = defaultPlace;
		this.viewFactory = viewFactory;
	}

	@Override
	public void init(CourseVersion courseVersion) {
		if (session.isInstitutionAdmin()) {
			view = viewFactory.getAdminCourseVersionContentView();
			view.setPresenter(this);
			
			boolean isWizardVersion = ContentSpec.WIZARD.equals(courseVersion.getContentSpec());
			if(isWizardVersion){			
				wizard = WizardMock.mockWizard();
				selectedWizardElement = wizard.getWizardTopics().get(0).getWizardSlides().get(0);
				view.init(courseVersion, wizard);
			} else {			
				view.init(courseVersion, null);
			}
			
		} else {
			logger.warning("Hey, only admins are allowed to see this! " + this.getClass().getName());
			placeController.goTo(defaultPlace);
		}
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	@Override
	public void wizardElementClicked(WizardElement wizardElement) {
		view.getWizardView().displaySlidePanel(false);
		this.selectedWizardElement = wizardElement;
		view.getWizardView().updateSidePanel();
		view.getWizardView().updateSlidePanel();
		view.getWizardView().displaySlidePanel(true);	
		LoadingPopup.hide();
	}

	@Override
	public AdminCourseVersionContentView getView() {
		return view;
	}

	@Override
	public void valueChanged(boolean valueHasChanged) {
		valueChanged(selectedWizardElement, valueHasChanged);
	}

	@Override
	public void valueChanged(WizardElement wizardElement, boolean valueHasChanged) {
		wizardElement.setValueChanged(valueHasChanged);
		view.getWizardView().updateSidePanel();
	}
	
	@Override
	public WizardElement getSelectedWizardElement(){
		return selectedWizardElement;
	}
	
	@Override
	public Wizard getWizard(){
		return wizard;
	}
}