package kornell.gui.client.presentation.sandbox.generic;

import kornell.gui.client.presentation.sandbox.SandboxView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class GenericSandboxView  extends Composite implements SandboxView {
	interface MyUiBinder extends UiBinder<Widget, GenericSandboxView> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
		
	public GenericSandboxView() {
		GWT.log("GenericSandboxView()");		
	    initWidget(uiBinder.createAndBindUi(this));
	}
	

	@Override
	public void setPresenter(Presenter presenter) {
		
	}
	

	
}
