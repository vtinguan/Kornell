package kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard;

public interface WizardSlideItem extends WizardElement {
	public static final String TYPE = WizardFactory.PREFIX+"slideItem+json";
	
	WizardSlideItemType getWizardSlideItemType();
	void setWizardSlideItemType(WizardSlideItemType wizardSlideItemType);
	
	String getText();
	void setText(String text);
	
	String getExtra();
	void setExtra(String extra);
	
}