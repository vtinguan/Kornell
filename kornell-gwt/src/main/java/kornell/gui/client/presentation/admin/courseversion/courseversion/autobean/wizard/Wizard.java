package kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard;

import java.util.List;

public interface Wizard {
	public static final String TYPE = WizardFactory.PREFIX+"wizard+json";

	String getUUID();
	void setUUID(String uuid);

	String getCourseVersionUUID();
	void setCourseVersionUUID(String courseVersionUUID);

    List<WizardTopic> getWizardTopics();
    void setWizardTopics(List<WizardTopic> wizardTopics);
	
}