package kornell.gui.client.presentation.uservoice.generic;


import kornell.gui.client.presentation.prototype.PrototypeView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class GenericUserVoiceView extends Composite implements PrototypeView {
	interface MyUiBinder extends UiBinder<Widget, GenericUserVoiceView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	public GenericUserVoiceView() {
		initWidget(uiBinder.createAndBindUi(this));
	}


	@Override
	public void setPresenter(Presenter presenter) {
	}

}
