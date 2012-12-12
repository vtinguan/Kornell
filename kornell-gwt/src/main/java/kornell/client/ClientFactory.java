package kornell.client;

import kornell.client.presenter.home.HomeView;
import kornell.client.presenter.vitrine.VitrineView;

public interface ClientFactory {
	//App Support
	App getApp();
	UserSession getUserSession();
	//Views
	HomeView getHomeView();
	VitrineView getVitrineView();
}
