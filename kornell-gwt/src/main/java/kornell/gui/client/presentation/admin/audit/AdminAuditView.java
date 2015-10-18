package kornell.gui.client.presentation.admin.audit;

import java.util.List;

import kornell.core.entity.CourseVersion;
import kornell.core.event.EntityChanged;
import kornell.gui.client.presentation.admin.PaginationPresenter;

import com.google.gwt.user.client.ui.IsWidget;

public interface AdminAuditView extends IsWidget {
	public interface Presenter extends PaginationPresenter {
	}
	void setPresenter(AdminAuditView.Presenter presenter);
	void setEntitiesChangedEvents(List<EntityChanged> entitiesChanged, Integer count, Integer searchCount);
}