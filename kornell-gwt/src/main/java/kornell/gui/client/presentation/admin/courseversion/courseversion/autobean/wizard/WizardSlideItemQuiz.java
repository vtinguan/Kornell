package kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard;

import java.util.List;

public interface WizardSlideItemQuiz extends WizardSlideItem {
	public static final String TYPE = WizardFactory.PREFIX+"slideItemQuiz+json";

	boolean getCountsTowardsCertificate();
	void setCountsTowardsCertificate(boolean countsTowardsCertificate);

	boolean getGivesInstantFeedback();
	void setGivesInstantFeedback(boolean givesInstantFeedback);

	Integer getRedosCount();
	void setRedosCount(Integer redosCount);
	
	List<WizardSlideItemQuizQuestion> getWizardSlideItemQuizQuestions();
	void setWizardSlideItemQuizQuestion(List<WizardSlideItemQuizQuestion> wizardSlideItemQuizQuestions);
	
}