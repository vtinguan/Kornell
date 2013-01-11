package kornell.gui.client.presentation.bar.generic;


import kornell.gui.client.presentation.bar.ActivityBarView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

//HTTP

public class GenericActivityBarView extends Composite implements ActivityBarView {
	interface MyUiBinder extends UiBinder<Widget, GenericActivityBarView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	public GenericActivityBarView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}

}
