package kornell.gui.client.presentation.atividade.generic;

import kornell.gui.client.presentation.atividade.AtividadeView;
import kornell.gui.client.scorm.event.NavigationRequest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericAtividadeView extends Composite 
	implements AtividadeView, NavigationRequest.Handler {
	interface MyUiBinder extends UiBinder<Widget, GenericAtividadeView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	FlowPanel contentPanel;

	private Presenter presenter;

	public GenericAtividadeView(EventBus eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		eventBus.addHandler(NavigationRequest.TYPE, this);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void onContinue(NavigationRequest event) {
		presenter.goContinue();
	}

	@Override
	public void onPrevious(NavigationRequest event) {
		presenter.goPrevious();
	}

	@Override
	public FlowPanel getContentPanel() {
		return contentPanel;
	}
}
