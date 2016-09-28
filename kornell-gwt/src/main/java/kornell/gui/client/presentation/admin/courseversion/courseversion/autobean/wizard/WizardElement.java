package kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard;

public interface WizardElement {
	public static final String TYPE = WizardFactory.PREFIX+"element+json";

	String getUUID();
	void setUUID(String uuid);

	String getTitle();
	void setTitle(String name);

	Integer getOrder();
	void setOrder(Integer order);
	
	boolean isValueChanged();
	void setValueChanged(boolean valueChanged);
	
}