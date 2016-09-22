package kornell.gui.client.presentation.admin.courseversion.courseversion;

import com.google.gwt.user.client.ui.IsWidget;

import kornell.core.entity.CourseVersion;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.Wizard;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardElement;

public interface AdminCourseVersionContentView extends IsWidget {
	public interface Presenter extends IsWidget {
		void init(CourseVersion courseVersion);
		void wizardElementClicked(Wizard wizard, WizardElement wizardElement);
	}
	void setPresenter(Presenter presenter);
	Presenter getPresenter();
	void init(CourseVersion courseVersion);
	void displaySlidePanel(boolean display);
	void updateSidePanel(Wizard wizard, WizardElement selectedWizardElement);
	void updateSlidePanel(Wizard wizard, WizardElement wizardElement);
}