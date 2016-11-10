package kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard;

public interface WizardSlideItemQuizQuestionOption extends WizardElement {
	public static final String TYPE = WizardFactory.PREFIX+"slideItemQuizQuestionOption+json";
	
	boolean getExpectedValue();
	void setExpectedValue(boolean expectedValue);
	
	
}