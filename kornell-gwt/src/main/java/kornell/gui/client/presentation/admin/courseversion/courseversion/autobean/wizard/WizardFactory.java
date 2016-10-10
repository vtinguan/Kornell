package kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface WizardFactory  extends AutoBeanFactory {
	public static String PREFIX = "application/vnd.kornell.v1.wz.";
	AutoBean<Wizard> newWizard();
	AutoBean<WizardElement> newWizardElement();
	AutoBean<WizardTopic> newWizardTopic();
	AutoBean<WizardSlide> newWizardSlide();
	AutoBean<WizardSlideItem> newWizardSlideItem();
	AutoBean<WizardSlideItemVideoLink> newWizardSlideItemVideoLink();
	AutoBean<WizardSlideItemImage> newWizardSlideItemImage();
}
