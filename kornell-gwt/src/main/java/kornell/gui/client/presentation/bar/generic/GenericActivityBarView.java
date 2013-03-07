package kornell.gui.client.presentation.bar.generic;

import kornell.gui.client.presentation.atividade.AtividadePlace;
import kornell.gui.client.presentation.bar.ActivityBarView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

//HTTP

public class GenericActivityBarView extends Composite implements
		ActivityBarView {
	interface MyUiBinder extends UiBinder<Widget, GenericActivityBarView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	public GenericActivityBarView(EventBus eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		eventBus.addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						Place newPlace = event.getNewPlace();
						boolean isAtAtividade = newPlace instanceof AtividadePlace;
						GenericActivityBarView.this.setVisible(isAtAtividade);
					}
				});

	}

	@Override
	public void setPresenter(Presenter presenter) {
	}

}
