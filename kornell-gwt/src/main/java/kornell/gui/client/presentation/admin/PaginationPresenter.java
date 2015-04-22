package kornell.gui.client.presentation.admin;

import com.google.gwt.user.client.ui.IsWidget;

public interface PaginationPresenter extends IsWidget{
	String getPageSize();
	void setPageSize(String pageSize);
	String getPageNumber();
	void setPageNumber(String pageNumber);
	String getSearchTerm();
	void setSearchTerm(String searchTerm);
	void updateData();
}
