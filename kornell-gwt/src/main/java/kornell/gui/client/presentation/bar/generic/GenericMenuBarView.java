package kornell.gui.client.presentation.bar.generic;


import kornell.gui.client.presentation.bar.MenuBarView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

//HTTP

public class GenericMenuBarView extends Composite implements MenuBarView {
	interface MyUiBinder extends UiBinder<Widget, GenericMenuBarView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	public GenericMenuBarView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	FlowPanel menuBar;


	@Override
	public void setPresenter(Presenter presenter) {
	}

}
