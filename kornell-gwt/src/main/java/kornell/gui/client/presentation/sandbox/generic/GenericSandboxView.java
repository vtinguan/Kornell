package kornell.gui.client.presentation.sandbox.generic;

import kornell.api.client.Callback;
import kornell.core.entity.Institution;
import kornell.gui.client.presentation.sandbox.SandboxView;
import kornell.gui.client.session.UserSession;

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
	
	@UiField FlowPanel panel;
	
	public GenericSandboxView() {
		GWT.log("GenericSandboxView()");		
	    initWidget(uiBinder.createAndBindUi(this));
	    panel.add(new Label("Loading some stuff..."));
	    UserSession.current(new Callback<UserSession>() {			
			@Override
			public void ok(UserSession session) {
				panel.add(new Label("I'm in institution "+session.getInstitutionUUID()));
				//session.hasRole(dean);
			}
		});
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		
	}
	

	
}
