package kornell.gui.client.presentation.message.compose;

import java.util.ArrayList;
import java.util.List;

import kornell.core.entity.Message;
import kornell.core.entity.RoleCategory;
import kornell.core.entity.RoleType;
import kornell.core.to.CourseClassTO;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;


public class GenericMessageComposeView extends Composite implements MessageComposeView {

	interface GenericMessageComposeUiBinder extends UiBinder<Widget, GenericMessageComposeView> {
	}

	private static GenericMessageComposeUiBinder uiBinder = GWT.create(GenericMessageComposeUiBinder.class);
	private static FormHelper formHelper = GWT.create(FormHelper.class);
	private static KornellConstants constants = GWT.create(KornellConstants.class);
	private MessageComposeView.Presenter presenter;

  KornellFormFieldWrapper recipient, subject, body;
	private List<KornellFormFieldWrapper> fields;
	private boolean initialized;
	
	@UiField Label lblTitle;
  @UiField FlowPanel fieldsPanel;
  @UiField Button btnOK;
  @UiField Button btnCancel;

	public GenericMessageComposeView() {
		initWidget(uiBinder.createAndBindUi(this));
    ensureDebugId("genericMessageComposeView");
	}
	
	@Override
	public void show(Message message){
			initialize(message);
	}
 
  private void initialize(Message message) {
  	lblTitle.setText(constants.compose());
  	
		this.fields = new ArrayList<KornellFormFieldWrapper>();
		fieldsPanel.clear();

		final ListBox recipients = new ListBox();

		
		
		List<CourseClassTO> courseClasses = Dean.getInstance().getCourseClassesTO().getCourseClasses();
		if(courseClasses != null){
			for (CourseClassTO courseClassTO : courseClasses) {
				recipients.addItem(constants.courseClassAdmin() + ": " + courseClassTO.getCourseClass().getName(), courseClassTO.getCourseClass().getUUID());
			}
		}
		 
		/*if(RoleCategory.hasRole(session.getCurrentUser().getRoles(), RoleType.courseClassAdmin) 
				|| session.isInstitutionAdmin()){
			recipients.addItem(constants.institutionAdmin() + ": " + Dean.getInstance().getInstitution().getFullName(), Dean.getInstance().getInstitution().getUUID());
		}*/

		recipient = new KornellFormFieldWrapper(constants.recipient(), new ListBoxFormField(recipients), true);
		fields.add(recipient);
		recipients.setSelectedIndex(0);
		fieldsPanel.add(recipient);
		
  	subject = new KornellFormFieldWrapper(constants.subject(), formHelper.createTextBoxFormField(message.getBody()), true);
		fields.add(subject);
		fieldsPanel.add(subject);

  	body = new KornellFormFieldWrapper(constants.body(), formHelper.createTextAreaFormField(message.getBody()), true);
		fields.add(body);
		fieldsPanel.add(body);
  }

	@Override
  protected void onEnsureDebugId(String baseID) {
		recipient.ensureDebugId(baseID + "-recipient");
  	subject.ensureDebugId(baseID + "-subject");
  	body.ensureDebugId(baseID + "-body");
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
	public KornellFormFieldWrapper getSubject() {
		return subject;
	}

  @Override
	public KornellFormFieldWrapper getBody() {
		return body;
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