package kornell.gui.client.presentation.message;

import java.util.List;

import kornell.core.to.ChatThreadMessageTO;
import kornell.core.to.ChatThreadMessagesTO;
import kornell.core.to.UnreadChatThreadTO;

import com.google.gwt.user.client.ui.IsWidget;

public interface MessageView  extends IsWidget {
	public interface Presenter extends IsWidget {
		void threadClicked(UnreadChatThreadTO unreadChatThreadTO);
		void sendMessage(String text);
		void filterAndShowThreads();
		void enableMessagesUpdate(boolean enable);
		void clearThreadSelection();
	}

	void setPresenter(Presenter presenter);
	void updateSidePanel(List<UnreadChatThreadTO> unreadChatThreadsTO, String selectedChatThreadUUID);
	void addMessagesToThreadPanel(ChatThreadMessagesTO to, String currentUserFullName);
	void updateThreadPanel(ChatThreadMessagesTO chatThreadMessagesTO, UnreadChatThreadTO unreadChatThreadTO, String currentUserFullName,
      boolean setFocus);
}
