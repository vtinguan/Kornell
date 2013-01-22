package kornell.gui.client;

import kornell.gui.client.presentation.activity.AtividadePresenter;
import kornell.gui.client.presentation.activity.AtividadeView;
import kornell.gui.client.presentation.home.HomeView;
import kornell.gui.client.presentation.vitrine.VitrineView;
import kornell.gui.client.presentation.welcome.WelcomeView;

public interface ClientFactory {
	ClientFactory startApp();
	//Views
	HomeView getHomeView();
	VitrineView getVitrineView();
	WelcomeView getWelcomeView();
	AtividadeView getActivityView();
	AtividadePresenter getActivityPresenter();
}
