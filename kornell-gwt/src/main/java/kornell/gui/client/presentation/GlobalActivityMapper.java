package kornell.gui.client.presentation;

import kornell.gui.client.ClientFactory;
import kornell.gui.client.presentation.admin.home.AdminHomeActivity;
import kornell.gui.client.presentation.admin.home.AdminHomePlace;
import kornell.gui.client.presentation.course.ClassroomActivity;
import kornell.gui.client.presentation.course.ClassroomPlace;
import kornell.gui.client.presentation.course.ClassroomPresenter;
import kornell.gui.client.presentation.course.library.CourseLibraryActivity;
import kornell.gui.client.presentation.course.library.CourseLibraryPlace;
import kornell.gui.client.presentation.course.library.CourseLibraryPresenter;
import kornell.gui.client.presentation.home.HomeActivity;
import kornell.gui.client.presentation.home.HomePlace;
import kornell.gui.client.presentation.profile.ProfileActivity;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.sandbox.SandboxActivity;
import kornell.gui.client.presentation.sandbox.SandboxPlace;
import kornell.gui.client.presentation.sandbox.SandboxPresenter;
import kornell.gui.client.presentation.terms.TermsActivity;
import kornell.gui.client.presentation.terms.TermsPlace;
import kornell.gui.client.presentation.vitrine.VitrineActivity;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.presentation.welcome.WelcomeActivity;
import kornell.gui.client.presentation.welcome.WelcomePlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

/**
 * A mapping of places to activities used by this application.
 */
public class GlobalActivityMapper implements ActivityMapper {
	private ClientFactory clientFactory;

	public GlobalActivityMapper(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	/** TODO: This may suck fast */
	public Activity getActivity(final Place place) {
		GWT.log("GlobalActivityMapper " + place.toString());
		
		if(!clientFactory.getKornellSession().isAuthenticated())
			return new VitrineActivity(clientFactory);
		
		// TODO: Cache and log mapping
		if (place instanceof HomePlace) {
			return new HomeActivity(clientFactory);
		}
		if (place instanceof VitrinePlace) {
			return new VitrineActivity(clientFactory);
		}
		if (place instanceof TermsPlace) {
			return new TermsActivity(clientFactory);
		}
		if (place instanceof WelcomePlace) {
			return new WelcomeActivity(clientFactory);
		}
		if (place instanceof ProfilePlace) {
			return new ProfileActivity(clientFactory);
		}
		if (place instanceof ClassroomPlace) {
			ClassroomPresenter coursePresenter = clientFactory.getViewFactory().getClassroomPresenter();
			coursePresenter.setPlace((ClassroomPlace) place);
			ClassroomActivity courseActivity = new ClassroomActivity(coursePresenter);
			return courseActivity;
		}
		if (place instanceof SandboxPlace) {
			SandboxPresenter sandboxPresenter = clientFactory.getViewFactory().getSandboxPresenter();
			sandboxPresenter.setPlace((SandboxPlace) place);
			return new SandboxActivity(sandboxPresenter);
		}
		//dean
		if (place instanceof AdminHomePlace) {
			return new AdminHomeActivity(clientFactory);
		}
		return null;
	}

}
