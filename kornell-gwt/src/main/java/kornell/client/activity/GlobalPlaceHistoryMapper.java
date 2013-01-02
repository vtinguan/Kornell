package kornell.client.activity;

import kornell.client.presenter.home.HomePlace;
import kornell.client.presenter.vitrine.VitrinePlace;
import kornell.client.presenter.welcome.WelcomePlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;


@WithTokenizers({HomePlace.Tokenizer.class,VitrinePlace.Tokenizer.class,WelcomePlace.Tokenizer.class})
public interface GlobalPlaceHistoryMapper extends PlaceHistoryMapper {
}
