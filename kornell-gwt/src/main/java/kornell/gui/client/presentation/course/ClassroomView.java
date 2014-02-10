package kornell.gui.client.presentation.course;


import kornell.core.lom.Contents;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

public interface ClassroomView  extends IsWidget{
	public interface Presenter extends IsWidget {
		Contents getContents();
	}

	FlowPanel getContentPanel();
	void setPresenter(Presenter presenter);
	void display(boolean isEnrolled);

}
