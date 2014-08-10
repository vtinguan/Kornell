package kornell.gui.client.presentation.message;

import kornell.gui.client.ClientFactory;

import com.google.gwt.user.client.ui.Widget;

public class MessagePresenter implements MessageView.Presenter{
	private MessageView view;
	private ClientFactory clientFactory;

	public MessagePresenter(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}
	
	@Override
	public Widget asWidget() {
		return view.asWidget();
	}
	
	private MessagePlace getPlace(){
		return (MessagePlace) clientFactory.getPlaceController().getWhere();
	}

}
