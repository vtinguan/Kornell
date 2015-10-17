package kornell.gui.client.presentation.profile.generic;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.presentation.profile.ProfileView;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.presentation.util.KornellNotification;
import kornell.gui.client.presentation.util.LoadingPopup;

import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class GenericSendMessageView extends Composite implements ProfileView {
	interface MyUiBinder extends UiBinder<Widget, GenericSendMessageView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private static KornellConstants constants = GWT.create(KornellConstants.class);

	private KornellSession session;
	private FormHelper formHelper;

	@UiField
	Modal sendMessageModal;
	@UiField
	FlowPanel sendMessageFields;
	@UiField
	Button btnOK;
	@UiField
	Button btnCancel;
	
	private UserInfoTO user;
	private TextArea modalMessageTextArea;
	private boolean initialized;


	public GenericSendMessageView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void show(){
		if(initialized)
			sendMessageModal.show();
	}

	public void initData(KornellSession session, UserInfoTO user, boolean isCurrentUser) {
		this.session = session;
		this.user = user;
		formHelper = new FormHelper();
		sendMessageFields.clear();
		initialized = true;

		btnOK.setText(constants.okButton().toUpperCase());
		btnCancel.setText(constants.cancelButton().toUpperCase());
		
		modalMessageTextArea = new TextArea();
		sendMessageFields.add(modalMessageTextArea);
		
		sendMessageFields.add(formHelper.getImageSeparator());
	}

	@UiHandler("btnOK")
	void doOK(ClickEvent e) { 
		if(modalMessageTextArea.getText().length() > 0){
			LoadingPopup.show();
			session.chatThreads().postMessageToDirectThread(modalMessageTextArea.getText(), user.getPerson().getUUID(), new Callback<Void>() {
				@Override
				public void ok(Void to) {
					LoadingPopup.hide();
					modalMessageTextArea.setText("");
					sendMessageModal.hide();
					KornellNotification.show(constants.messageSentSuccess());
				}
			});

		}
	}

	@UiHandler("btnCancel")
	void doCancel(ClickEvent e) {
		sendMessageModal.hide();
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}

}