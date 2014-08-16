package kornell.gui.client.presentation.message.compose;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.api.client.MessagesClient;
import kornell.core.entity.Message;
import kornell.gui.client.ViewFactory;

import com.google.gwt.user.client.ui.Widget;

public class MessageComposePresenter implements MessageComposeView.Presenter{
	private MessageComposeView view;
	private KornellSession session;
	private MessagesClient messagesClient;

	public MessageComposePresenter(KornellSession session, ViewFactory viewFactory, MessagesClient messagesClient) {
		view = viewFactory.getMessageComposeView();
		this.session = session;
		this.messagesClient = messagesClient;
	}
	
	@Override
	public void okButtonClicked(){
		Message message = view.getMessage();
		messagesClient.create(message, new Callback<Message>() {

			@Override
      public void ok(Message message) {
	      // TODO Auto-generated method stub
	      
      }
		});
	}
	
	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

}
