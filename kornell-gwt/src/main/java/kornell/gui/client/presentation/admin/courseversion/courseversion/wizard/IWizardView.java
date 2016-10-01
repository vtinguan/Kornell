package kornell.gui.client.presentation.admin.courseversion.courseversion.wizard;

public interface IWizardView {
	public void updateWizard();
	public boolean validateFields();
	public boolean refreshForm();
	public void resetFormToOriginalValues();
}
