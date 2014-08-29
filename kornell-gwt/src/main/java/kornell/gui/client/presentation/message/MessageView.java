package kornell.gui.client.presentation.message;

import java.util.List;

import kornell.core.to.ChatThreadMessageTO;
import kornell.core.to.UnreadChatThreadTO;

import com.google.gwt.user.client.ui.IsWidget;

public interface MessageView  extends IsWidget {
	public interface Presenter extends IsWidget {

		void threadClicked(UnreadChatThreadTO unreadChatThreadTO);

		void sendMessage(String text);

	}

	void setPresenter(Presenter presenter);

	void updateSidePanel(List<UnreadChatThreadTO> unreadChatThreadsTO, String selectedChatThreadUUID);

	void updateThreadPanel(List<ChatThreadMessageTO> chatThreadMessageTOs, UnreadChatThreadTO unreadChatThreadTO, String currentUserFullName);
}
