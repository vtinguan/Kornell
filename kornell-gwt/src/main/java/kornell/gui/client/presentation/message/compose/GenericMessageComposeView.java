package kornell.gui.client.presentation.message.compose;

import java.util.ArrayList;
import java.util.List;

import kornell.api.client.KornellSession;
import kornell.core.entity.RoleType;
import kornell.core.to.CourseClassTO;
import kornell.core.to.RoleTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.util.view.formfield.KornellFormFieldWrapper;
import kornell.gui.client.util.view.formfield.ListBoxFormField;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class GenericMessageComposeView extends Composite implements MessageComposeView {

	interface GenericMessageComposeUiBinder extends UiBinder<Widget, GenericMessageComposeView> {
	}

	private static GenericMessageComposeUiBinder uiBinder = GWT.create(GenericMessageComposeUiBinder.class);
	private static FormHelper formHelper = GWT.create(FormHelper.class);
	private static KornellConstants constants = GWT.create(KornellConstants.class);
	private MessageComposeView.Presenter presenter;

	KornellFormFieldWrapper recipient, messageText;
	private List<KornellFormFieldWrapper> fields;
	private KornellSession session;

	@UiField
	Label lblTitle;
	@UiField
	Label lblSubTitle;
	@UiField
	Image separatorBar;
	@UiField
	FlowPanel fieldsPanel;
	@UiField
	Button btnOK;
	@UiField
	Button btnCancel;

	public GenericMessageComposeView(KornellSession session) {
		this.session = session;
		initWidget(uiBinder.createAndBindUi(this));
		ensureDebugId("genericMessageComposeView");
	}

	@Override
	public void show(String courseClassUUID) {
		initialize(courseClassUUID);
	}

	private void initialize(String courseClassUUID) {
		lblTitle.setText(constants.composeTitle());
		lblSubTitle.setText(constants.composeSubTitle());
		separatorBar.setUrl(FormHelper.SEPARATOR_BAR_IMG_PATH);
		separatorBar.addStyleName(FormHelper.SEPARATOR_BAR_CLASS);

		this.fields = new ArrayList<KornellFormFieldWrapper>();
		fieldsPanel.clear();

		boolean hasNonCourseClassThreadAcces = false;
		final ListBox recipients = new ListBox();
		
		for (RoleTO roleTO : session.getCurrentUser().getRoles()) {
			if(RoleType.courseClassAdmin.equals(roleTO.getRole().getRoleType())){
				recipients.addItem("Responsável pela instituição" + ": " + Dean.getInstance().getInstitution().getName() , "i-" + roleTO.getRole().getCourseClassAdminRole().getCourseClassUUID());
				hasNonCourseClassThreadAcces = true;
			}
			else if(RoleType.institutionAdmin.equals(roleTO.getRole().getRoleType())){
				recipients.addItem("Responsável pela plataforma", "");
				hasNonCourseClassThreadAcces = true;
			}
		}

		for (CourseClassTO courseClassTO : Dean.getInstance().getHelpCourseClasses()) {
			recipients.addItem(constants.courseClassAdmin() + ": " + courseClassTO.getCourseClass().getName(), courseClassTO.getCourseClass().getUUID());
		}

		if (courseClassUUID == null && recipients.getItemCount() <= 0)
			this.setVisible(false);

		recipients.setSelectedValue(courseClassUUID);
		recipient = new KornellFormFieldWrapper(constants.recipient(), new ListBoxFormField(recipients), recipients.getItemCount() > 1 && (hasNonCourseClassThreadAcces || courseClassUUID == null));
		fields.add(recipient);
		fieldsPanel.add(recipient);

		messageText = new KornellFormFieldWrapper(constants.message(), formHelper.createTextAreaFormField(""), true);
		fields.add(messageText);
		fieldsPanel.add(messageText);
	}

	@Override
	protected void onEnsureDebugId(String baseID) {
		recipient.ensureDebugId(baseID + "-recipient");
		messageText.ensureDebugId(baseID + "-messageText");
		btnOK.ensureDebugId(baseID + "-btnOK");
		btnCancel.ensureDebugId(baseID + "-btnCancel");
	}

	@UiHandler("btnOK")
	void onOkButtonClicked(ClickEvent e) {
		presenter.okButtonClicked();
	}

	@UiHandler("btnCancel")
	void onCancelButtonClicked(ClickEvent e) {
		presenter.cancelButtonClicked();
	}

	@Override
	public void setPresenter(Presenter p) {
		presenter = p;
	}

	@Override
	public KornellFormFieldWrapper getRecipient() {
		return recipient;
	}

	@Override
	public KornellFormFieldWrapper getMessageText() {
		return messageText;
	}

	@Override
	public boolean checkErrors() {
		return formHelper.checkErrors(fields);
	}

	@Override
	public void clearErrors() {
		formHelper.clearErrors(fields);
	}
}