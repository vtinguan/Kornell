package kornell.gui.client.presentation.sandbox.generic;

import kornell.gui.client.presentation.sandbox.SandboxView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class GenericSandboxView  extends Composite implements SandboxView {
	interface MyUiBinder extends UiBinder<Widget, GenericSandboxView> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	VerticalPanel vpanel = new VerticalPanel();
	
	public GenericSandboxView() {
		GWT.log("GenericSandboxView()");		
		panel.add(vpanel);
		vpanel.add(new Label("Loading..."));
	    initWidget(uiBinder.createAndBindUi(this));
	    fetchAndDraw();
	}
	
	private void fetchAndDraw() {
		panel.clear();
		vpanel.add(new Label("done"));
		
	}

	@UiField FlowPanel panel;
	

	@Override
	public void setPresenter(Presenter presenter) {
		
	}
	

	
}
