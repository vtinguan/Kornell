package kornell.gui.client.presentation.bar.generic;


import kornell.gui.client.presentation.PresentationUtils;
import kornell.gui.client.presentation.bar.ActivityBarView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

//HTTP

public class GenericActivityBarView extends Composite implements ActivityBarView {
	interface MyUiBinder extends UiBinder<Widget, GenericActivityBarView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	public GenericActivityBarView(EventBus eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		PresentationUtils.invisibleOnVitrine(eventBus,this);
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}

}
