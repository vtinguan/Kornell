package kornell.gui.client;

import kornell.gui.client.presentation.activity.ActivityPlace;
import kornell.gui.client.presentation.activity.ActivityPresenter;
import kornell.gui.client.presentation.activity.ActivityView;
import kornell.gui.client.presentation.home.HomeView;
import kornell.gui.client.presentation.vitrine.VitrineView;
import kornell.gui.client.presentation.welcome.WelcomeView;

public interface ClientFactory {
	ClientFactory startApp();
	//Views
	HomeView getHomeView();
	VitrineView getVitrineView();
	WelcomeView getWelcomeView();
	ActivityView getActivityView();
	ActivityPresenter getActivityPresenter(ActivityPlace place);
}
