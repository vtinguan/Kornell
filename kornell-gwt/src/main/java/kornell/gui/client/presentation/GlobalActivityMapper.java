package kornell.gui.client.presentation;


import kornell.gui.client.ClientFactory;
import kornell.gui.client.presentation.atividade.AtividadeActivity;
import kornell.gui.client.presentation.atividade.AtividadePlace;
import kornell.gui.client.presentation.atividade.AtividadePresenter;
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
import kornell.gui.client.presentation.course.notes.CourseNotesActivity;
import kornell.gui.client.presentation.course.notes.CourseNotesPlace;
import kornell.gui.client.presentation.course.notes.CourseNotesPresenter;
import kornell.gui.client.presentation.course.specialists.CourseSpecialistsActivity;
import kornell.gui.client.presentation.course.specialists.CourseSpecialistsPlace;
import kornell.gui.client.presentation.course.specialists.CourseSpecialistsPresenter;
import kornell.gui.client.presentation.home.HomeActivity;
import kornell.gui.client.presentation.home.HomePlace;
import kornell.gui.client.presentation.terms.TermsActivity;
import kornell.gui.client.presentation.terms.TermsPlace;
import kornell.gui.client.presentation.vitrine.VitrineActivity;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.presentation.welcome.WelcomeActivity;
import kornell.gui.client.presentation.welcome.WelcomePlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.shared.GWT;
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
	GWT.log("Global Manager looling for "+place.toString());
    if (place instanceof HomePlace) {
      return new HomeActivity(factory);
    }
    if (place instanceof VitrinePlace){
    	return new VitrineActivity(factory);
    }
	if (place instanceof TermsPlace) {
		return new TermsActivity(factory);
	}
	if (place instanceof WelcomePlace) {
		return new WelcomeActivity(factory);
	}
	if (place instanceof CourseHomePlace) {
		CourseHomePresenter coursePresenter = factory.getCourseHomePresenter();
		coursePresenter.setPlace((CourseHomePlace)place);
		return new CourseHomeActivity(coursePresenter);
	}
	if (place instanceof CourseDetailsPlace) {
		CourseDetailsPresenter courseDetailsPresenter = factory.getCourseDetailsPresenter();
		courseDetailsPresenter.setPlace((CourseDetailsPlace)place);
		return new CourseDetailsActivity(courseDetailsPresenter);
	}
	if (place instanceof CourseLibraryPlace) {
		CourseLibraryPresenter courseLibraryPresenter = factory.getCourseLibraryPresenter();
		courseLibraryPresenter.setPlace((CourseLibraryPlace)place);
		return new CourseLibraryActivity(courseLibraryPresenter);
	}
	if (place instanceof CourseForumPlace) {
		CourseForumPresenter courseForumPresenter = factory.getCourseForumPresenter();
		courseForumPresenter.setPlace((CourseForumPlace)place);
		return new CourseForumActivity(courseForumPresenter);
	}
	if (place instanceof CourseChatPlace) {
		CourseChatPresenter courseChatPresenter = factory.getCourseChatPresenter();
		courseChatPresenter.setPlace((CourseChatPlace)place);
		return new CourseChatActivity(courseChatPresenter);
	}
	if (place instanceof CourseSpecialistsPlace) {
		CourseSpecialistsPresenter courseSpecialistsPresenter = factory.getCourseSpecialistsPresenter();
		courseSpecialistsPresenter.setPlace((CourseSpecialistsPlace)place);
		return new CourseSpecialistsActivity(courseSpecialistsPresenter);
	}
	if (place instanceof CourseNotesPlace) {
		CourseNotesPresenter courseNotesPresenter = factory.getCourseNotesPresenter();
		courseNotesPresenter.setPlace((CourseNotesPlace)place);
		return new CourseNotesActivity(courseNotesPresenter);
	}
	if (place instanceof AtividadePlace) {
		AtividadePresenter atividadePresenter = factory.getActivityPresenter();
		atividadePresenter.setPlace((AtividadePlace)place);
		return new AtividadeActivity(atividadePresenter);
	}

    return null;
  }
}