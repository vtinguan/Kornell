package kornell.gui.client.presentation.atividade.generic;

import kornell.gui.client.presentation.course.ClassroomView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericCourseClassView extends Composite 
	implements ClassroomView {
	interface MyUiBinder extends UiBinder<Widget, GenericCourseClassView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	FlowPanel contentPanel;

	private Presenter presenter;

	public GenericCourseClassView(EventBus eventBus) {
		GWT.log("new GenericCourseView");
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public FlowPanel getContentPanel() {
		return contentPanel;
	}
}
