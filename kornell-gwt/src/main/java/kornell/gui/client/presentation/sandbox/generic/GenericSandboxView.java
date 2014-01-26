package kornell.gui.client.presentation.sandbox.generic;

import kornell.api.client.Callback;
import kornell.api.client.UserSession;
import kornell.core.entity.Institution;
import kornell.core.entity.RoleType;
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
	
	@UiField FlowPanel panel;
	
	public GenericSandboxView() {
		GWT.log("GenericSandboxView()");		
	    initWidget(uiBinder.createAndBindUi(this));
	    panel.add(new Label("Loading some stuff..."));
	    UserSession.current(new Callback<UserSession>(){			
			@Override
			public void ok(UserSession session) {
				if(session.isAuthenticated())
					panel.add(new Label("And i am authenticated as "+session.getUserInfo().getPerson().getFullName()));
				else
					panel.add(new Label("And i am not authenticated."));
				
				if(session.isDean())
					panel.add(new Label("And i am a dean."));
				else
					panel.add(new Label("And i am not dean."));
			}
		});
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		
	}
	

	
}
