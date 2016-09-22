package kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;

public class WizardMock {

	public static final WizardFactory WIZARD_FACTORY = GWT.create(WizardFactory.class);
	
	public static Wizard mockWizard(){				
		Wizard wizard = WIZARD_FACTORY.newWizard().as();

		List<WizardTopic> wizardTopics = new ArrayList<>();
		wizardTopics.add(mockWizardTopic(1));
		wizardTopics.add(mockWizardTopic(2));
		wizardTopics.add(mockWizardTopic(3));
		
		wizard.setWizardTopics(wizardTopics);
		
		return wizard;
	}

	private static WizardTopic mockWizardTopic(int order) {
		WizardTopic wizardTopic = WIZARD_FACTORY.newWizardTopic().as();
		
		wizardTopic.setUUID(order + "");		
		wizardTopic.setOrder(order);		
		wizardTopic.setTitle("I am topic " + order);

		List<WizardSlide> wizardSlides = new ArrayList<>();
		wizardSlides.add(mockWizardSlide(wizardTopic, 1));
		wizardSlides.add(mockWizardSlide(wizardTopic, 2));
		wizardSlides.add(mockWizardSlide(wizardTopic, 3));
		
		wizardTopic.setWizardSlides(wizardSlides);
		
		return wizardTopic;
	}

	private static WizardSlide mockWizardSlide(WizardTopic wizardTopic, int order) {
		WizardSlide wizardSlide = WIZARD_FACTORY.newWizardSlide().as();
		
		wizardSlide.setUUID(wizardTopic.getUUID() + "." + order);		
		wizardSlide.setOrder(order);		
		wizardSlide.setTitle("I am slide " + wizardSlide.getUUID());
		
		List<WizardSlideItem> wizardSlideItems = new ArrayList<>();
		wizardSlideItems.add(mockWizardSlideItemText(wizardSlide, 1));
		wizardSlideItems.add(mockWizardSlideItemText(wizardSlide, 2));
		
		wizardSlide.setWizardSlideItems(wizardSlideItems);
		
		return wizardSlide;
	}

	private static WizardSlideItem mockWizardSlideItemGeneric(WizardSlide wizardSlide, int order) {
		WizardSlideItem wizardSlideItem = WIZARD_FACTORY.newWizardSlideItem().as();		
		wizardSlideItem.setUUID(wizardSlide.getUUID() + "." + order);		
		wizardSlideItem.setOrder(order);		
		wizardSlideItem.setTitle("I am item " + wizardSlideItem.getUUID());
		wizardSlideItem.setText("Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum ");
		return wizardSlideItem;
	}

	private static WizardSlideItem mockWizardSlideItemText(WizardSlide wizardSlide, int order) {
		WizardSlideItem wizardSlideItem = mockWizardSlideItemGeneric(wizardSlide, order);
		wizardSlideItem.setWizardSlideItemType(WizardSlideItemType.text);
		return wizardSlideItem;
	}

}
