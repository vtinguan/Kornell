package kornell.gui.client.presentation.message.compose;

import java.util.Date;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.api.client.MessagesClient;
import kornell.core.entity.EntityFactory;
import kornell.core.entity.Message;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.presentation.util.KornellNotification;

import com.google.gwt.user.client.ui.Widget;

public class MessageComposePresenter implements MessageComposeView.Presenter {
	private MessageComposeView view;
	private KornellSession session;
	private MessagesClient messagesClient;
	private EntityFactory entityFactory;
	
	private Message message;

	public MessageComposePresenter(KornellSession session, ViewFactory viewFactory, MessagesClient messagesClient, EntityFactory entityFactory) {
		view = viewFactory.getMessageComposeView();
		this.entityFactory = entityFactory;
		this.session = session;
		this.messagesClient = messagesClient;
		
		Message message = this.entityFactory.newMessage().as();
		message.setSubject("");
		message.setBody("");
		message.setParentMessageUUID(null);
		
		setMessage(message);
	}

	private void setMessage(Message message) {
		this.message = message;
		//view.setBody(message.getBody());
		//view.setSubject(message.getSubject());
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
		updateMessageFromUI();
		if(validateMessage()){
			messagesClient.create(message, new Callback<Message>() {
				@Override
				public void ok(Message message) {
					KornellNotification.show("Mensagem enviada com sucesso!");
				}
			});
		}
	}

	private boolean validateMessage() {
	  return false;
  }

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

}
