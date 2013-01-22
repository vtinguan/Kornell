package kornell.gui.client.presentation.activity;

import com.google.gwt.activity.shared.AbstractActivity;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * Activity  = GWT Activity
 * Atividade = SCORM Activity
 * 
 * Got it?
 * 
 * @author jfaerman
 */
public class AtividadeActivity extends AbstractActivity {
	
	private AtividadePresenter presenter;

	public AtividadeActivity(AtividadePresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		panel.setWidget(presenter);
		
	}
}