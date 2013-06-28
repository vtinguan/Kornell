
package kornell.gui.client;

import kornell.gui.client.presentation.atividade.AtividadePresenter;
import kornell.gui.client.presentation.atividade.AtividadeView;
import kornell.gui.client.presentation.course.CoursePresenter;
import kornell.gui.client.presentation.course.CourseView;
import kornell.gui.client.presentation.home.HomeView;
import kornell.gui.client.presentation.terms.TermsView;
import kornell.gui.client.presentation.vitrine.VitrineView;
import kornell.gui.client.presentation.welcome.WelcomeView;

public interface ClientFactory {
	ClientFactory startApp();
	//Views
	HomeView getHomeView();
	VitrineView getVitrineView();
	TermsView getTermsView();
	WelcomeView getWelcomeView();
	CourseView getCourseView();
	CoursePresenter getCoursePresenter();
	AtividadeView getActivityView();
	AtividadePresenter getActivityPresenter();
}