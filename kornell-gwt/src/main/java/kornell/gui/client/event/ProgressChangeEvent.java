package kornell.gui.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ProgressChangeEvent extends GwtEvent<ProgressChangeEventHandler>{

	public static Type<ProgressChangeEventHandler> TYPE = new Type<ProgressChangeEventHandler>();
	
	private Integer totalPages;
	private Integer currentPage;
	private Integer pagesVisitedCount;
	private String enrollmentUUID;
	
	@Override
	public Type<ProgressChangeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ProgressChangeEventHandler handler) {
		handler.onProgressChange(this);		
	}
	
	public boolean hasNext(){
		return currentPage < totalPages;
	}
	
	public boolean hasPrevious(){
		return currentPage > 1;
	}

	public Integer getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getProgressPercent() {
		return (pagesVisitedCount * 100)/totalPages;
	}

	public void setPagesVisitedCount(int pagesVisitedCount) {
		this.pagesVisitedCount = pagesVisitedCount;
	}

	public Integer getPagesVisitedCount() {
		return this.pagesVisitedCount;
	}

	public String getEnrollmentUUID() {
		return enrollmentUUID;
	}

	public void setEnrollmentUUID(String enrollmentUUID) {
		this.enrollmentUUID = enrollmentUUID;
	}
}