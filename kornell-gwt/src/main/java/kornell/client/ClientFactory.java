package kornell.client;

import kornell.client.presenter.home.HomeView;
import kornell.client.presenter.vitrine.VitrineView;
import kornell.client.presenter.welcome.WelcomeView;

public interface ClientFactory {
	//App Support
	App getApp();
	UserSession getUserSession();
	//Views
	HomeView getHomeView();
	VitrineView getVitrineView();
	WelcomeView getWelcomeView();
}
