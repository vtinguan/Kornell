package kornell.gui.client.presentation.welcome.generic;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.api.client.data.Person;
import kornell.gui.client.presentation.welcome.WelcomeView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

public class GenericWelcomeView extends Composite implements WelcomeView{
	Label widget = new Label("Welcome...");
	
	public GenericWelcomeView(KornellClient client) {
		GWT.log("GenericWelcomeView()");
		initWidget(widget);
		client.getCurrentUser(new Callback(){
			@Override
			protected void ok(Person person) {
				widget.setText("Bem vindo "+person.getFullName());
			}
		});
	}

	@Override
	public void setPresenter(Presenter presenter) {}

}
