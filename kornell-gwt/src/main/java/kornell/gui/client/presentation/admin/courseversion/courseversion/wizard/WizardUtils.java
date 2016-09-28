package kornell.gui.client.presentation.admin.courseversion.courseversion.wizard;

import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.Wizard;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardElement;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlide;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItem;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardTopic;

public class WizardUtils {


	public static WizardElement findWizardElementByUUID(Wizard wizard, String wizardElementUUID){
		for(WizardTopic topic : wizard.getWizardTopics()){
			if(wizardElementUUID.equals(topic.getUUID())) return topic;
			for(WizardSlide slide : topic.getWizardSlides()){
				if(wizardElementUUID.equals(slide.getUUID())) return slide;
				for(WizardSlideItem item : slide.getWizardSlideItems()){
					if(wizardElementUUID.equals(item.getUUID())) return item;
				}
			}
		}
		return null;
	}
	
	public static boolean wizardElementHasValueChanged(WizardElement wizardElement){
		if(wizardElement instanceof WizardTopic)
			return wizardTopicHasValueChanged((WizardTopic) wizardElement);
		else if(wizardElement instanceof WizardSlide)
			return wizardSlideHasValueChanged((WizardSlide) wizardElement);
		else if(wizardElement instanceof WizardSlideItem)
			return wizardSlideItemHasValueChanged((WizardSlideItem) wizardElement);
		else
			return false;
	}
	
	public static boolean wizardTopicHasValueChanged(WizardTopic wizardTopic){
		boolean anySlideHasValueChanged = false;
		/*for(WizardSlide slide : wizardTopic.getWizardSlides()){
			anySlideHasValueChanged = anySlideHasValueChanged || wizardSlideHasValueChanged(slide);
		}*/
		return wizardTopic.isValueChanged() || anySlideHasValueChanged;
	}
	
	public static boolean wizardSlideHasValueChanged(WizardSlide wizardSlide){
		boolean anyItemHasValueChanged = false;
		for(WizardSlideItem item : wizardSlide.getWizardSlideItems()){
			anyItemHasValueChanged = anyItemHasValueChanged || wizardSlideItemHasValueChanged(item);
		}
		return wizardSlide.isValueChanged() || anyItemHasValueChanged;
	}
	
	public static boolean wizardSlideItemHasValueChanged(WizardSlideItem wizardSlideItem){
		return wizardSlideItem.isValueChanged();
	}
}
