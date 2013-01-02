package kornell.gui.client.presentation.welcome.generic;

import kornell.gui.client.presentation.welcome.WelcomeView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

public class GenericWelcomeView extends Composite implements WelcomeView{

	
	public GenericWelcomeView() {
		GWT.log("GenericWelcomeView()");
		initWidget(new Label("Welcome, sucker!!!!"));
	}

	@Override
	public void setPresenter(Presenter presenter) {}

}
