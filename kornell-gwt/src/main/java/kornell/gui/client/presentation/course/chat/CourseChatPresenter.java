package kornell.gui.client.presentation.course.chat;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;

public class CourseChatPresenter implements CourseChatView.Presenter{
	CourseChatView view;
	private CourseChatPlace place;
	
	public CourseChatPresenter(CourseChatView view,
							 PlaceController placeCtrl) {
		this.view = view;
		view.setPresenter(this);
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	public void setPlace(CourseChatPlace place) {
		this.place = place;
	}
}
