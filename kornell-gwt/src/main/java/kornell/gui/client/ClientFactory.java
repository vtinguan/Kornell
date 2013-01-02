package kornell.gui.client;

import kornell.gui.client.presentation.home.HomeView;
import kornell.gui.client.presentation.vitrine.VitrineView;
import kornell.gui.client.presentation.welcome.WelcomeView;

public interface ClientFactory {
	//App Support
	App getApp();
	//Views
	HomeView getHomeView();
	VitrineView getVitrineView();
	WelcomeView getWelcomeView();
}
