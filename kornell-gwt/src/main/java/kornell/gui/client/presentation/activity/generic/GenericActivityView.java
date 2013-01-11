package kornell.gui.client.presentation.activity.generic;

import kornell.gui.client.presentation.activity.ActivityView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

public class GenericActivityView extends Composite implements ActivityView {
	interface MyUiBinder extends UiBinder<Widget, GenericActivityView> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	@UiField Frame frmActivity;
	
	public GenericActivityView() {
	    initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		// TODO Auto-generated 
	}

	public void display(String displayUrl) {
		frmActivity.setUrl(displayUrl);
	}

}
