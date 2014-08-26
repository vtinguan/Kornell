package kornell.gui.client.presentation.message.inbox;

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

public class MessageInboxPresenter implements MessageInboxView.Presenter {
	private FormHelper formHelper = GWT.create(FormHelper.class);
	private KornellConstants constants = GWT.create(KornellConstants.class);
	
	private MessageInboxView view;
	private KornellSession session;

	public MessageInboxPresenter(KornellSession session) {
		this.session = session;
		if(view == null){
			view = new GenericMessageInboxView();
			view.setPresenter(this);
		}
	}

	@Override
	public void okButtonClicked() {
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

}
