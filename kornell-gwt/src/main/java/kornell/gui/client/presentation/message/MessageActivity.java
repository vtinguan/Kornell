package kornell.gui.client.presentation.message;

import kornell.gui.client.ClientFactory;
import kornell.gui.client.personnel.Dean;
import kornell.gui.client.presentation.bar.generic.GenericMenuBarView;
import kornell.gui.client.presentation.profile.ProfilePlace;
import kornell.gui.client.presentation.terms.TermsPlace;
import kornell.gui.client.presentation.vitrine.VitrinePlace;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class MessageActivity extends AbstractActivity{
	private static MessagePresenter presenter;
	
	public MessageActivity(ClientFactory clientFactory) {
	    if(presenter == null){
	    	presenter = clientFactory.getViewFactory().getMessagePresenter();
	    }
	 }
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		panel.setWidget(presenter);		
	}

}
