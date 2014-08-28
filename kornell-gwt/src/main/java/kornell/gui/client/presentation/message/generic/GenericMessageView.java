package kornell.gui.client.presentation.message.generic;

import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.message.MessageView;
import kornell.gui.client.presentation.message.MessageView.Presenter;
import kornell.gui.client.presentation.util.FormHelper;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;


public class GenericMessageView extends Composite implements MessageView {

	interface GenericMessageUiBinder extends UiBinder<Widget, GenericMessageView> {
	}

	private static GenericMessageUiBinder uiBinder = GWT.create(GenericMessageUiBinder.class);
	private static FormHelper formHelper = GWT.create(FormHelper.class);
	private static KornellConstants constants = GWT.create(KornellConstants.class);
	private MessageView.Presenter presenter;


  @UiField FlowPanel sidePanel;
  @UiField FlowPanel threadPanel;

	public GenericMessageView() {
		initWidget(uiBinder.createAndBindUi(this));
    ensureDebugId("genericMessageInboxView");
	}
	
	@Override
  protected void onEnsureDebugId(String baseID) {
		sidePanel.ensureDebugId(baseID + "-sidePanel");
		threadPanel.ensureDebugId(baseID + "-threadPanel");
  }

	@Override
	public void setPresenter(Presenter p) {
		presenter = p;
	}
}