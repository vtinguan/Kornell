package kornell.client;

import kornell.client.presenter.home.HomeView;
import kornell.client.presenter.vitrine.VitrineView;

public interface ClientFactory {
	App getApp();

	HomeView getHomeView();

	VitrineView getVitrineView();
}
