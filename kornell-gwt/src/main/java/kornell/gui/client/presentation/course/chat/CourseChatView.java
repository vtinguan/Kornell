package kornell.gui.client.presentation.course.chat;

import com.google.gwt.user.client.ui.IsWidget;

public interface CourseChatView  extends IsWidget {
	public interface Presenter extends IsWidget {
	}

	void setPresenter(Presenter presenter);
}
