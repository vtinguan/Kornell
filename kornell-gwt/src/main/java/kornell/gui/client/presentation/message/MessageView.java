package kornell.gui.client.presentation.message;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import kornell.core.to.ChatThreadMessagesTO;
import kornell.core.to.UnreadChatThreadTO;

public interface MessageView  extends IsWidget {
	public interface Presenter extends IsWidget {
		void threadClicked(UnreadChatThreadTO unreadChatThreadTO);
		void sendMessage(String text);
		void filterAndShowThreads();
		void enableMessagesUpdate(boolean enable);
		void clearThreadSelection();
		MessagePanelType getMessagePanelType();
		void onScrollToTop(boolean scrollToBottomAfterFetchingMessages);
		void scrollToBottom();
	}

	void setPresenter(Presenter presenter);
	void updateSidePanel(List<UnreadChatThreadTO> unreadChatThreadsTO, String selectedChatThreadUUID, String currentUserFullName);
	void updateThreadPanel(UnreadChatThreadTO unreadChatThreadTO, String currentUserFullName);
	void addMessagesToThreadPanel(ChatThreadMessagesTO chatThreadMessagesTO, String currentUserFullName, boolean insertOnTop);
	void scrollToBottom();
	void setPlaceholder(String placeholder);
	void setMessagePanelType(MessagePanelType messagePanelType);
	void displayThreadPanel(boolean display);
	void sendSidePanelItemToTop(String chatThreadUUID);
}
