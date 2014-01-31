package kornell.gui.client.presentation;

import kornell.api.client.UserSession;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.presentation.admin.home.AdminHomeActivity;
import kornell.gui.client.presentation.admin.home.AdminHomePlace;
import kornell.gui.client.presentation.course.ClassroomActivity;
import kornell.gui.client.presentation.course.ClassroomPlace;
import kornell.gui.client.presentation.course.ClassroomPresenter;
import kornell.gui.client.presentation.course.chat.CourseChatActivity;
import kornell.gui.client.presentation.course.chat.CourseChatPlace;
import kornell.gui.client.presentation.course.chat.CourseChatPresenter;
import kornell.gui.client.presentation.course.course.CourseHomeActivity;
import kornell.gui.client.presentation.course.course.CourseHomePlace;
import kornell.gui.client.presentation.course.course.CourseHomePresenter;
import kornell.gui.client.presentation.course.details.CourseDetailsActivity;
import kornell.gui.client.presentation.course.details.CourseDetailsPlace;
import kornell.gui.client.presentation.course.details.CourseDetailsPresenter;
import kornell.gui.client.presentation.course.forum.CourseForumActivity;
import kornell.gui.client.presentation.course.forum.CourseForumPlace;
import kornell.gui.client.presentation.course.forum.CourseForumPresenter;
import kornell.gui.client.presentation.course.library.CourseLibraryActivity;
import kornell.gui.client.presentation.course.library.CourseLibraryPlace;
import kornell.gui.client.presentation.course.library.CourseLibraryPresenter;
import kornell.gui.client.presentation.course.specialists.CourseSpecialistsActivity;
import kornell.gui.client.presentation.course.specialists.CourseSpecialistsPlace;
import kornell.gui.client.presentation.course.specialists.CourseSpecialistsPresenter;
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
	private ClientFactory factory;

	public GlobalActivityMapper(ClientFactory clientFactory) {
		this.factory = clientFactory;
	}

	/** TODO: This may suck fast */
	public Activity getActivity(final Place place) {
		GWT.log("GlobalActivityMapper " + place.toString());
		
		// TODO: Cache and log mapping
		if (place instanceof HomePlace) {
			return new HomeActivity(factory);
		}
		if (place instanceof VitrinePlace) {
			return new VitrineActivity(factory);
		}
		if (place instanceof TermsPlace) {
			return new TermsActivity(factory);
		}
		if (place instanceof WelcomePlace) {
			return new WelcomeActivity(factory);
		}
		if (place instanceof ProfilePlace) {
			return new ProfileActivity(factory);
		}
		if (place instanceof CourseHomePlace) {
			CourseHomePresenter coursePresenter = factory
					.getViewFactory().getCourseHomePresenter();
			coursePresenter.setPlace((CourseHomePlace) place);
			return new CourseHomeActivity(coursePresenter);
		}
		if (place instanceof CourseLibraryPlace) {
			CourseLibraryPresenter courseLibraryPresenter = factory
					.getViewFactory().getCourseLibraryPresenter();
			courseLibraryPresenter.setPlace((CourseLibraryPlace) place);
			return new CourseLibraryActivity(courseLibraryPresenter);
		}
		if (place instanceof CourseForumPlace) {
			CourseForumPresenter courseForumPresenter = factory
					.getViewFactory().getCourseForumPresenter();
			courseForumPresenter.setPlace((CourseForumPlace) place);
			return new CourseForumActivity(courseForumPresenter);
		}
		if (place instanceof CourseChatPlace) {
			CourseChatPresenter courseChatPresenter = factory
					.getViewFactory().getCourseChatPresenter();
			courseChatPresenter.setPlace((CourseChatPlace) place);
			return new CourseChatActivity(courseChatPresenter);
		}
		if (place instanceof CourseSpecialistsPlace) {
			CourseSpecialistsPresenter courseSpecialistsPresenter = factory
					.getViewFactory().getCourseSpecialistsPresenter();
			courseSpecialistsPresenter.setPlace((CourseSpecialistsPlace) place);
			return new CourseSpecialistsActivity(courseSpecialistsPresenter);
		}

		if (place instanceof ClassroomPlace) {
			ClassroomPresenter coursePresenter = factory.getViewFactory().getClassroomPresenter();
			coursePresenter.setPlace((ClassroomPlace) place);
			ClassroomActivity courseActivity = new ClassroomActivity(coursePresenter);
			return courseActivity;
		}

		if (place instanceof SandboxPlace) {
			SandboxPresenter sandboxPresenter = factory.getViewFactory().getSandboxPresenter();
			sandboxPresenter.setPlace((SandboxPlace) place);
			return new SandboxActivity(sandboxPresenter);
		}
		//dean
		if (place instanceof AdminHomePlace) {
			return new AdminHomeActivity(factory);
		}
		return null;
	}

}