package kornell.gui.client;

import kornell.gui.client.presentation.atividade.AtividadePresenter;
import kornell.gui.client.presentation.atividade.AtividadeView;
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
