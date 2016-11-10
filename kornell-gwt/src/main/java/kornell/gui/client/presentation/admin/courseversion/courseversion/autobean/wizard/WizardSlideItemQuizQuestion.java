package kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard;

import java.util.List;

public interface WizardSlideItemQuizQuestion extends WizardElement {
	public static final String TYPE = WizardFactory.PREFIX+"slideItemQuizQuestion+json";

	List<WizardSlideItemQuizQuestionOption> getWizardSlideItemQuizQuestionOptions();
	void setWizardSlideItemQuizQuestionOption(List<WizardSlideItemQuizQuestionOption> wizardSlideItemQuizQuestionOptions);
	
	WizardSlideItemQuizQuestionType getWizardSlideItemQuizQuestionType();
	void setWizardSlideItemQuizQuestionType(WizardSlideItemQuizQuestionType wizardSlideItemQuizQuestionType);
	
}