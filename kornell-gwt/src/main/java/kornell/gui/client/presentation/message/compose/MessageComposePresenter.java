package kornell.gui.client.presentation.message.compose;

import java.util.Date;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.api.client.MessagesClient;
import kornell.core.entity.EntityFactory;
import kornell.core.entity.Message;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.personnel.MrPostman;
import kornell.gui.client.presentation.util.KornellNotification;

import com.google.gwt.user.client.ui.Widget;

public class MessageComposePresenter implements MessageComposeView.Presenter {
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
		updateMessageFromUI();
		if(validateMessage()){
			messagesClient.create(message, new Callback<Message>() {
				@Override
				public void ok(Message message) {
					KornellNotification.show("Mensagem enviada com sucesso!");
					MrPostman.hide();
				}
			});
		}
	}

	@Override
  public void cancelButtonClicked() {
		MrPostman.hide();
  }

	private boolean validateMessage() {
	  return true;
  }

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

}
