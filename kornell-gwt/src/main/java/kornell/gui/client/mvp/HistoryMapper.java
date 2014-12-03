package kornell.gui.client.mvp;

import kornell.gui.client.presentation.admin.courseClasses.AdminCourseClassesPlace;
import kornell.gui.client.presentation.admin.home.AdminHomePlace;
import kornell.gui.client.presentation.admin.institution.AdminInstitutionPlace;
import kornell.gui.client.presentation.course.ClassroomPlace;
import kornell.gui.client.presentation.home.HomePlace;
import kornell.gui.client.presentation.message.MessagePlace;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.sandbox.SandboxPlace;
import kornell.gui.client.presentation.terms.TermsPlace;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.presentation.welcome.WelcomePlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;


@WithTokenizers({HomePlace.Tokenizer.class,
	VitrinePlace.Tokenizer.class,
	TermsPlace.Tokenizer.class,
	WelcomePlace.Tokenizer.class,
	ProfilePlace.Tokenizer.class,
	MessagePlace.Tokenizer.class,
	ClassroomPlace.Tokenizer.class,
	SandboxPlace.Tokenizer.class,
	AdminHomePlace.Tokenizer.class,
	AdminCourseClassesPlace.Tokenizer.class,
	AdminInstitutionPlace.Tokenizer.class})
public interface HistoryMapper extends PlaceHistoryMapper {
}
