package kornell.client.activity;

import kornell.client.presenter.home.HomePlace;
import kornell.client.presenter.vitrine.VitrinePlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;


@WithTokenizers({HomePlace.Tokenizer.class,VitrinePlace.Tokenizer.class})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
