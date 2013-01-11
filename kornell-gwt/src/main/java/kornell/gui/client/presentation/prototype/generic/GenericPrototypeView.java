package kornell.gui.client.presentation.prototype.generic;


import kornell.gui.client.presentation.prototype.PrototypeView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

//HTTP

public class GenericPrototypeView extends Composite implements PrototypeView {
	interface MyUiBinder extends UiBinder<Widget, GenericPrototypeView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	public GenericPrototypeView() {
		initWidget(uiBinder.createAndBindUi(this));
	}


	@Override
	public void setPresenter(Presenter presenter) {
	}

}
