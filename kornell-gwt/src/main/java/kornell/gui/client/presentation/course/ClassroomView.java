package kornell.gui.client.presentation.course;


import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

public interface ClassroomView  extends IsWidget{
	public interface Presenter extends IsWidget {
	
	}

	FlowPanel getContentPanel();
	void setPresenter(Presenter presenter);

}
