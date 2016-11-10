package kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard;

import java.util.List;

public interface WizardTopic extends WizardElement {
	public static final String TYPE = WizardFactory.PREFIX+"topic+json";

    List<WizardSlide> getWizardSlides();
    void setWizardSlides(List<WizardSlide> wizardSlides);
	
}