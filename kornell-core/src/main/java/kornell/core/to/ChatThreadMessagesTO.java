package kornell.core.to;

import java.util.Date;
import java.util.List;

public interface ChatThreadMessagesTO {
	public static final String TYPE = TOFactory.PREFIX + "chatThreadMessages+json";
	
	List<ChatThreadMessageTO> getChatThreadMessageTOs();
	void setChatThreadMessageTOs(List<ChatThreadMessageTO> chatThreadMessageTOs);

	String getServerTime();
	void setServerTime(String serverTime);
	
}