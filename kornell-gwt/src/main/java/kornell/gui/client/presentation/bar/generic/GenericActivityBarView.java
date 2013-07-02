package kornell.gui.client.presentation.bar.generic;

import kornell.gui.client.content.NavigationRequest;
import kornell.gui.client.presentation.bar.ActivityBarView;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;


public class GenericActivityBarView extends Composite implements ActivityBarView {
	
	interface MyUiBinder extends UiBinder<Widget, GenericActivityBarView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	@UiField
	Button btnNext;
	@UiField
	Button btnPrev;

	private EventBus bus;
	
	public GenericActivityBarView(EventBus bus) {
		initWidget(uiBinder.createAndBindUi(this));
		this.bus = bus;

	}
	
	@UiHandler("btnNext")
	public void btnNextClicked(ClickEvent e){
		bus.fireEvent(NavigationRequest.next());
	}

	@UiHandler("btnPrev")
	public void btnPrevClicked(ClickEvent e){
		bus.fireEvent(NavigationRequest.prev());		
	}

	@UiField
	FlowPanel activityBar;
	
	@Override
	public void setPresenter(Presenter presenter) {
	}

}
