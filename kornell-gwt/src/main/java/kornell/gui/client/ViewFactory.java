
package kornell.gui.client;

import kornell.gui.client.presentation.admin.home.AdminHomeView;
import kornell.gui.client.presentation.bar.MenuBarView;
import kornell.gui.client.presentation.bar.SouthBarView;
import kornell.gui.client.presentation.course.ClassroomPresenter;
import kornell.gui.client.presentation.course.ClassroomView;
import kornell.gui.client.presentation.course.library.CourseLibraryPresenter;
import kornell.gui.client.presentation.course.library.CourseLibraryView;
import kornell.gui.client.presentation.home.HomeView;
import kornell.gui.client.presentation.profile.ProfileView;
import kornell.gui.client.presentation.sandbox.SandboxPresenter;
import kornell.gui.client.presentation.sandbox.SandboxView;
import kornell.gui.client.presentation.terms.TermsView;
import kornell.gui.client.presentation.vitrine.VitrineView;
import kornell.gui.client.presentation.welcome.WelcomeView;

import com.google.gwt.user.client.ui.SimplePanel;

public interface ViewFactory {
	
	void initGUI();
	
	SimplePanel getShell();
	
	MenuBarView getMenuBarView();
	SouthBarView getSouthBarView(); 
	
	HomeView getHomeView();
	VitrineView getVitrineView();
	TermsView getTermsView();
	WelcomeView getWelcomeView();
	ProfileView getProfileView();
	
	CourseLibraryView getCourseLibraryView();
	CourseLibraryPresenter getCourseLibraryPresenter();
	
	
	ClassroomView getClassroomView();
	ClassroomPresenter getClassroomPresenter();
	SandboxView getSandboxView();
	SandboxPresenter getSandboxPresenter();
	
	//dean
	AdminHomeView getDeanHomeView();

}
