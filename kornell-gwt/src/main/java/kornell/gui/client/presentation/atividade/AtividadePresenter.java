package kornell.gui.client.presentation.atividade;

import kornell.gui.client.content.SequencerFactory;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.NodeList;

public class AtividadePresenter implements AtividadeView.Presenter{
	AtividadeView view;
	private AtividadePlace place;
	private SequencerFactory sequencer;
	
	public AtividadePresenter(AtividadeView view,
							 PlaceController placeCtrl,
							 SequencerFactory sequencer) {
		this.view = view;
		view.setPresenter(this);
		this.sequencer = sequencer;		
	}
	

	private void displayPlace() {
		sequencer.at(place).displayOn(getPanel());
	}


	private FlowPanel getPanel() {
		return view.getContentPanel();
	}


	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	public void setPlace(AtividadePlace place) {
		this.place = place;
		displayPlace();
	}
}
