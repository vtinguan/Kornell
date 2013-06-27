package kornell.gui.client.presentation;

import kornell.gui.client.presentation.atividade.AtividadePlace;
import kornell.gui.client.presentation.course.CoursePlace;
import kornell.gui.client.presentation.home.HomePlace;
import kornell.gui.client.presentation.terms.TermsPlace;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.presentation.welcome.WelcomePlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;


@WithTokenizers({HomePlace.Tokenizer.class,
	VitrinePlace.Tokenizer.class,
	TermsPlace.Tokenizer.class,
	WelcomePlace.Tokenizer.class,
	CoursePlace.Tokenizer.class,
	AtividadePlace.Tokenizer.class})
public interface HistoryMapper extends PlaceHistoryMapper {
}
