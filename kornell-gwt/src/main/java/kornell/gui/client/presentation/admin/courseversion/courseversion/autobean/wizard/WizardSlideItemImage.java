package kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard;

public interface WizardSlideItemImage extends WizardSlideItem {
	public static final String TYPE = WizardFactory.PREFIX+"slideItemImage+json";
	
	String getURL();
	void setURL(String url);
	
}