package kornell.gui.client.presentation.message.inbox;

import java.util.ArrayList;
import java.util.List;

import kornell.core.entity.ChatThread;
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


public class GenericMessageInboxView extends Composite implements MessageInboxView {

	interface GenericMessageInboxUiBinder extends UiBinder<Widget, GenericMessageInboxView> {
	}

	private static GenericMessageInboxUiBinder uiBinder = GWT.create(GenericMessageInboxUiBinder.class);
	private static FormHelper formHelper = GWT.create(FormHelper.class);
	private static KornellConstants constants = GWT.create(KornellConstants.class);
	private MessageInboxView.Presenter presenter;


  @UiField FlowPanel sidePanel;
  @UiField FlowPanel threadPanel;

	public GenericMessageInboxView() {
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