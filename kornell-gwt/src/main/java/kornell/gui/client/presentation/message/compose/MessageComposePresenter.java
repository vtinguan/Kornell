package kornell.gui.client.presentation.message.compose;

import java.util.Date;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.api.client.MessagesClient;
import kornell.core.entity.EntityFactory;
import kornell.core.entity.Message;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.personnel.MrPostman;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.presentation.util.KornellNotification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

public class MessageComposePresenter implements MessageComposeView.Presenter {
	private FormHelper formHelper = GWT.create(FormHelper.class);
	private KornellConstants constants = GWT.create(KornellConstants.class);
	
	private MessageComposeView view;
	private KornellSession session;
	private MessagesClient messagesClient;
	private EntityFactory entityFactory;
	private ViewFactory viewFactory;
	
	private Message message;

	public MessageComposePresenter(KornellSession session, ViewFactory viewFactory, EntityFactory entityFactory) {
		this.entityFactory = entityFactory;
		this.session = session;
		this.messagesClient = session.messages();
		this.viewFactory = viewFactory;
		
		
	}

	@Override
	public void init(Message message) {
		if(view == null){
			view = viewFactory.getMessageComposeView();
			view.setPresenter(this);
		}
		
		if(message == null){
			message = this.entityFactory.newMessage().as();
			message.setSubject("");
			message.setBody("");
			message.setParentMessageUUID(null);
		}
		
		this.message = message;
		view.show(message);
	}

	private Message updateMessageFromUI() {
		message.setBody(view.getBody().getFieldPersistText());
		message.setSubject(view.getSubject().getFieldPersistText());
		message.setSenderUUID(session.getCurrentUser().getPerson().getUUID());
		message.setSentAt(new Date());
		return message;
	}

	@Override
	public void okButtonClicked() {
		if(validateMessage()){
			updateMessageFromUI();
			Callback<Message> messageCallback = new Callback<Message>() {
				@Override
				public void ok(Message message) {
					KornellNotification.show("Mensagem enviada com sucesso!");
					MrPostman.hide();
				}
			};
			String entityUUID = view.getRecipient().getFieldPersistText();
			if(view.getRecipient().getFieldDisplayText().indexOf(constants.institutionAdmin() + ": ") >= 0){
				messagesClient.sendMessageToInstitutionAdmin(message, entityUUID, messageCallback);				
			} else {
				messagesClient.sendMessageToCourseClassAdmin(message, Dean.getInstance().getInstitution().getUUID(), entityUUID, messageCallback);
			}
		}
	}

	@Override
  public void cancelButtonClicked() {
		MrPostman.hide();
  }
	
	private boolean validateMessage() {	
		view.clearErrors();
		if (!formHelper.isLengthValid(view.getSubject().getFieldPersistText(), 2, 100)) {
			view.getSubject().setError("Preencha o assunto");
		}
		
		if (!formHelper.isLengthValid(view.getBody().getFieldPersistText(), 2, 1000)) {
			view.getBody().setError("Preencha o corpo da mensagem");
		}

		return !view.checkErrors();
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

}
