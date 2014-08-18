package kornell.gui.client.presentation.message.compose;

import kornell.gui.client.util.view.formfield.KornellFormFieldWrapper;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;


public class GenericMessageComposeView extends Composite implements MessageComposeView {

	interface GenericMessageComposeUiBinder extends UiBinder<Widget, GenericMessageComposeView> {
	}

	private static GenericMessageComposeUiBinder uiBinder = GWT.create(GenericMessageComposeUiBinder.class);
	private MessageComposeView.Presenter presenter;

  KornellFormFieldWrapper subject, body;
  @UiField FlowPanel fieldsPanel;
  @UiField Button btnOK;
  @UiField Button btnCancel;

	public GenericMessageComposeView() {
		initWidget(uiBinder.createAndBindUi(this));
    ensureDebugId("genericMessageComposeView");
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