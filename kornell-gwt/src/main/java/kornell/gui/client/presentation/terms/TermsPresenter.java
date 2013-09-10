package kornell.gui.client.presentation.terms;

import kornell.gui.client.ClientFactory;

import com.google.gwt.user.client.ui.Widget;

public class TermsPresenter implements TermsView.Presenter{
	private TermsView view;

	public TermsPresenter(ClientFactory clientFactory) {
		view = clientFactory.getTermsView();
		view.setPresenter(this);
	}
	
	@Override
	public Widget asWidget() {
		Widget termsView = getView().asWidget();
		return termsView;
	}
	
	private TermsView getView() {
		return view;
	}

}
