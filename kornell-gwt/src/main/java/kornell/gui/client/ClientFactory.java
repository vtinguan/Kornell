
package kornell.gui.client;

import kornell.api.client.KornellClient;
import kornell.core.entity.EntityFactory;
import kornell.core.entity.Institution;
import kornell.core.event.EventFactory;
import kornell.core.lom.LOMFactory;
import kornell.core.to.TOFactory;
import kornell.gui.client.presentation.course.CoursePresenter;
import kornell.gui.client.presentation.course.CourseView;
import kornell.gui.client.presentation.course.chat.CourseChatPresenter;
import kornell.gui.client.presentation.course.chat.CourseChatView;
import kornell.gui.client.presentation.course.course.CourseHomePresenter;
import kornell.gui.client.presentation.course.course.CourseHomeView;
import kornell.gui.client.presentation.course.details.CourseDetailsPresenter;
import kornell.gui.client.presentation.course.details.CourseDetailsView;
import kornell.gui.client.presentation.course.forum.CourseForumPresenter;
import kornell.gui.client.presentation.course.forum.CourseForumView;
import kornell.gui.client.presentation.course.library.CourseLibraryPresenter;
import kornell.gui.client.presentation.course.library.CourseLibraryView;
import kornell.gui.client.presentation.course.specialists.CourseSpecialistsPresenter;
import kornell.gui.client.presentation.course.specialists.CourseSpecialistsView;
import kornell.gui.client.presentation.home.HomeView;
import kornell.gui.client.presentation.profile.ProfileView;
import kornell.gui.client.presentation.sandbox.SandboxPresenter;
import kornell.gui.client.presentation.sandbox.SandboxView;
import kornell.gui.client.presentation.terms.TermsView;
import kornell.gui.client.presentation.vitrine.VitrineView;
import kornell.gui.client.presentation.welcome.WelcomeView;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.web.bindery.event.shared.EventBus;

public interface ClientFactory {
	
	ClientFactory startApp();
	
	KornellClient getKornellClient();
	PlaceController getPlaceController();
	PlaceHistoryMapper getHistoryMapper();
	EventBus getEventBus();
	Place getDefaultPlace();
	Institution getInstitution();

	EntityFactory getEntityFactory();
	TOFactory getTOFactory();
	LOMFactory getLOMFactory();
	EventFactory getEventFactory();
	
	//Views
	HomeView getHomeView();
	VitrineView getVitrineView();
	TermsView getTermsView();
	WelcomeView getWelcomeView();
	ProfileView getProfileView();
	
	
	CourseHomeView getCourseHomeView();
	CourseHomePresenter getCourseHomePresenter();
	CourseDetailsView getCourseDetailsView();
	CourseDetailsPresenter getCourseDetailsPresenter();
	CourseLibraryView getCourseLibraryView();
	CourseLibraryPresenter getCourseLibraryPresenter();
	CourseForumView getCourseForumView();
	CourseForumPresenter getCourseForumPresenter();
	CourseChatView getCourseChatView();
	CourseChatPresenter getCourseChatPresenter();
	CourseSpecialistsView getCourseSpecialistsView();
	CourseSpecialistsPresenter getCourseSpecialistsPresenter();
	
	
	CourseView getCourseView();
	CoursePresenter getCoursePresenter();
	SandboxView getSandboxView();
	SandboxPresenter getSandboxPresenter();
}