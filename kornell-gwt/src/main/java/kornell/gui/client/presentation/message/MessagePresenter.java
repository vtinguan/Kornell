package kornell.gui.client.presentation.message;

import kornell.gui.client.ClientFactory;

import com.google.gwt.user.client.ui.Widget;

public class MessagePresenter implements MessageView.Presenter{
	private MessageView view;
	private ClientFactory clientFactory;
	private String viewType;

	public MessagePresenter(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}
	
	@Override
	public Widget asWidget() {
		Widget messageView = null;
    try {
	    messageView = getView().asWidget();
    } catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
		return messageView;
	}
	
	private MessageView getView() throws Exception {
		this.viewType = getPlace().getViewType();
		if(MessagePlace.INBOX.equals(viewType))
			view = clientFactory.getViewFactory().getMessageView();
		else if(MessagePlace.ARCHIVED.equals(viewType))
			view = clientFactory.getViewFactory().getMessageView();
		else if(MessagePlace.COMPOSE.equals(viewType))
			view = clientFactory.getViewFactory().getMessageView();
		else
			throw new Exception("Unknown view type!");
		view.setPresenter(this);
		return view;
	}
	
	private MessagePlace getPlace(){
		return (MessagePlace) clientFactory.getPlaceController().getWhere();
	}

	@Override
	public String getViewType() {
	  return viewType;
  }

}
