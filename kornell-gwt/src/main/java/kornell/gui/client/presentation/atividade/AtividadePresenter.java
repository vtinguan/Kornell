package kornell.gui.client.presentation.atividade;

import kornell.gui.client.content.RendererFactory;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.NodeList;

public class AtividadePresenter implements AtividadeView.Presenter{
	AtividadeView view;
	private AtividadePlace place;
	private PlaceController placeCtrl;
	private NodeList items;
	private RendererFactory renderer;
	
	public AtividadePresenter(AtividadeView view,
							 PlaceController placeCtrl,
							 RendererFactory renderer) {
		this.placeCtrl = placeCtrl;
		this.view = view;
		view.setPresenter(this);
		this.renderer = renderer;		
	}
	

	private void displayPlace() {
		final String uuid = place.getCourseUUID();
		final Integer position = place.getPosition();
		renderer.source(uuid,position)
				.render(view.getContentPanel());
		
	}


	@Override
	public Widget asWidget() {
		return view.asWidget();
	}


	@Override
	public void goContinue() {
		if(place.getPosition().compareTo(items.getLength()-1) < 0)
		placeCtrl.goTo(place.next());
	}


	@Override
	public void goPrevious() {
		if(place.getPosition().compareTo(new Integer("0")) > 0)
		placeCtrl.goTo(place.previous());
	}


	public void setPlace(AtividadePlace place) {
		this.place = place;
		displayPlace();
	}
}
