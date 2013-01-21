package kornell.gui.client.presentation.activity.generic;

import kornell.gui.client.presentation.activity.ActivityView;
import kornell.gui.client.scorm.API_1484_11;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.gui.client.scorm.event.NavigationRequest;

public class GenericActivityView extends Composite 
	implements ActivityView, NavigationRequest.Handler {
	interface MyUiBinder extends UiBinder<Widget, GenericActivityView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Frame frmActivity;

	private EventBus eventBus;

	private API_1484_11 scormAPI;

	public GenericActivityView(EventBus eventBus, API_1484_11 scormAPI) {
		this.eventBus = eventBus;
		this.scormAPI = scormAPI;
		initWidget(uiBinder.createAndBindUi(this));
	}

	private void initSCORM() {
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				scormAPI.bind();
			}
		});
		
		eventBus.addHandler(NavigationRequest.TYPE, this);
		
	}

	@Override
	public void setPresenter(Presenter presenter) {
		// TODO: Use MVP
	}

	public void display(String displayUrl) {
		frmActivity.setUrl(displayUrl);
		initSCORM();
	}

	@Override
	public void onContinue(NavigationRequest event) {
		GWT.log("CONTINUE!!!!!");
	}

	@Override
	public void onPrevious(NavigationRequest event) {
		GWT.log("PREVIOUS!!!!!");
	}

}
