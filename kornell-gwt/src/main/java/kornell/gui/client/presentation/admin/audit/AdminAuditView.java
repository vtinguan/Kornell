package kornell.gui.client.presentation.admin.audit;

import com.google.gwt.user.client.ui.IsWidget;

import kornell.core.to.EntityChangedEventsTO;
import kornell.gui.client.util.view.PaginationPresenter;

public interface AdminAuditView extends IsWidget {
	public interface Presenter extends PaginationPresenter {
	}
	void setPresenter(AdminAuditView.Presenter presenter);
	void setEntitiesChangedEvents(EntityChangedEventsTO entityChangedEventsTO);
	EntityChangedEventsTO getEntityChangedEventsTO();
}