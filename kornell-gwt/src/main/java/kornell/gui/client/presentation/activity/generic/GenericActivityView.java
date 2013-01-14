package kornell.gui.client.presentation.activity.generic;

import kornell.gui.client.presentation.activity.ActivityView;
import kornell.gui.client.scorm.API_1484_11;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

public class GenericActivityView extends Composite implements ActivityView {
	interface MyUiBinder extends UiBinder<Widget, GenericActivityView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Frame frmActivity;

	public GenericActivityView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	private void initSCORM() {
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				API_1484_11 api = API_1484_11.create();
				GWT.log("agora valendo");
				injectAPI(frmActivity.getElement(), api);
			}
		});
	}

	private native void injectAPI(Element iframe, API_1484_11 api) /*-{
		$wnd.API_1484_11 = {
			"Initialize" : function() {
				api.@kornell.gui.client.scorm.API_1484_11::Initialize()();
			}
		}
	}-*/;

	@Override
	public void setPresenter(Presenter presenter) {
		// TODO Auto-generated
	}

	public void display(String displayUrl) {
		frmActivity.setUrl(displayUrl);
		initSCORM();
	}

}
