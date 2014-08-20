package kornell.gui.client.presentation.message.compose;

import java.util.ArrayList;
import java.util.List;

import kornell.core.entity.Message;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.util.view.formfield.KornellFormFieldWrapper;

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
	private MessageComposeView.Presenter presenter;

  KornellFormFieldWrapper subject, body;
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
  	lblTitle.setText("Escrever Mensagem");
  	
		this.fields = new ArrayList<KornellFormFieldWrapper>();
		fieldsPanel.clear();
		
  	subject = new KornellFormFieldWrapper("Assunto", formHelper.createTextBoxFormField(message.getBody()), true);
		fields.add(subject);
		fieldsPanel.add(subject);

  	body = new KornellFormFieldWrapper("Mensagem", formHelper.createTextAreaFormField(message.getBody()), true);
		fields.add(body);
		fieldsPanel.add(body);
  }

	@Override
  protected void onEnsureDebugId(String baseID) {
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
	public KornellFormFieldWrapper getSubject() {
		return subject;
	}

  @Override
	public KornellFormFieldWrapper getBody() {
		return body;
	}
}