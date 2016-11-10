package kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard;

public interface WizardSlideItemVideoLink extends WizardSlideItem {
	public static final String TYPE = WizardFactory.PREFIX+"slideItemImage+json";
	
	String getVideoLinkType();
	void setVideoLinkType(String videoLinkType);
	
	String getURL();
	void setURL(String url);
	
}