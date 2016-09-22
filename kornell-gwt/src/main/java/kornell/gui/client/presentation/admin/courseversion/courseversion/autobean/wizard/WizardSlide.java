package kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard;

import java.util.List;

public interface WizardSlide extends WizardElement {
	public static final String TYPE = WizardFactory.PREFIX+"slide+json";

    List<WizardSlideItem> getWizardSlideItems();
    void setWizardSlideItems(List<WizardSlideItem> wizardSlideItems);
	
}