package kornell.gui.client.presentation.message.compose;

import java.util.Date;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.api.client.ChatThreadsClient;
import kornell.core.entity.EntityFactory;
import kornell.core.entity.ChatThread;
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
	private ChatThreadsClient threadsClient;
	private EntityFactory entityFactory;
	private ViewFactory viewFactory;
	
	private String message;

	public MessageComposePresenter(KornellSession session, ViewFactory viewFactory, EntityFactory entityFactory) {
		this.entityFactory = entityFactory;
		this.session = session;
		this.threadsClient = session.messages();
		this.viewFactory = viewFactory;
	}

	@Override
	public void init() {
		if(view == null){
			view = viewFactory.getMessageComposeView();
			view.setPresenter(this);
		}
		
		view.show();
	}

	@Override
	public void okButtonClicked() {
		if(validateMessage()){
			String messageText = view.getMessageText().getFieldPersistText();
			Callback<ChatThread> messageCallback = new Callback<ChatThread>() {
				@Override
				public void ok(ChatThread message) {
					KornellNotification.show("Mensagem enviada com sucesso!");
					MrPostman.hide();
				}
			};
			String entityUUID = view.getRecipient().getFieldPersistText();
			threadsClient.postMessageToCourseClassThread(message, entityUUID, messageCallback);
		}
	}

	@Override
  public void cancelButtonClicked() {
		MrPostman.hide();
  }
	
	private boolean validateMessage() {	
		view.clearErrors();		
		if (!formHelper.isLengthValid(view.getMessageText().getFieldPersistText(), 1, 1000)) {
			view.getMessageText().setError("Preencha o corpo da mensagem.");
		}
		return !view.checkErrors();
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

}
