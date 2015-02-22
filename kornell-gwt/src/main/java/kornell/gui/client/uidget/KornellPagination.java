package kornell.gui.client.uidget;

import java.util.List;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.Pagination;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

@SuppressWarnings({"rawtypes", "unchecked"})
public class KornellPagination extends Pagination{

	
	private int pageSize = 20;
	private int maxPaginationTabs = 10;
	private int totalRowCount = 0;
	private CellTable table;
	private List rowData;

	public KornellPagination(CellTable table, List rowData) {
		this.table = table;
		this.rowData = rowData;
	}

	public KornellPagination(CellTable table, List rowData, int pageSize) {
		this(table, rowData);
		this.pageSize = pageSize;
	}
	
	public void displayTableData(int pageNumber) {		
		int end = pageNumber * pageSize;
		int start = end - pageSize; 
		
    	if(end > totalRowCount)
    		end = totalRowCount;
    	
		table.setRowData(rowData.subList(start, end));
		updatePagination(pageNumber);
		
	}

	private void updatePagination(int page) {
		clear();
    	int navLinkCount = (totalRowCount+pageSize-1)/pageSize;
    	setVisible(navLinkCount > 1);
    	setAlignment("centered");
    	
    	if(navLinkCount < maxPaginationTabs){
    		for(int i = 1; i <= navLinkCount; i++){
    			add(createNavLink(i, i==page));
    		}
    	} else if(page <= (maxPaginationTabs / 2)){
    		for(int i = 1; i <= maxPaginationTabs; i++){
    			add(createNavLink(i, i==page));
    		}
    		add(createNavLink(">", navLinkCount));
    	} else if(page > navLinkCount - (maxPaginationTabs / 2)){
    		add(createNavLink("<", 1));
    		for(int i = (navLinkCount - maxPaginationTabs) + 1; i <= navLinkCount; i++){
    			add(createNavLink(i, i==page));
    		}
    	} else {
    		add(createNavLink("<", 1));
    		for(int i = page - (maxPaginationTabs / 2); i <= page + (maxPaginationTabs / 2); i++){
    			add(createNavLink(i, i==page));
    		}
    		add(createNavLink(">", navLinkCount));
    	}
	}
	
	private NavLink createNavLink(int i, boolean active) {
		return createNavLink(""+i, i, active);
	}
	
	private NavLink createNavLink(String text, int pageNumber) {
		return createNavLink(text, pageNumber, false);
	}

	private NavLink createNavLink(String text, int pageNumber, boolean active) {
		NavLink navLink = new NavLink();
		navLink.setText(text);
		navLink.setName(""+pageNumber);
		navLink.setActive(active);
		if(pageNumber > 0){
			navLink.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					IconAnchor thisThing = (IconAnchor) event.getSource();
					displayTableData(Integer.parseInt(thisThing.getName().trim()));
				}
			});
		}
		return navLink;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		displayTableData(1);
	}
	
	public int getPageSize() {
		return this.pageSize;
	}
	
	public int getTotalRowCount() {
		return totalRowCount;
	}
	
	public void setRowData(List rowData) {
		this.rowData = rowData;
		this.totalRowCount = rowData.size();
	}	
	
}
