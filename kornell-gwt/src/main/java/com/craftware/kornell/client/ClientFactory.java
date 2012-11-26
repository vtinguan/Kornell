package com.craftware.kornell.client;

import com.craftware.kornell.client.presenter.home.HomeView;
import com.craftware.kornell.client.presenter.vitrine.VitrineView;

public interface ClientFactory {
	App getApp();

	HomeView getHomeView();

	VitrineView getVitrineView();
}
