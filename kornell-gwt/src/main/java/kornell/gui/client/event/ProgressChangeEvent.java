package kornell.gui.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ProgressChangeEvent extends GwtEvent<ProgressChangeEventHandler>{

	public static Type<ProgressChangeEventHandler> TYPE = new Type<ProgressChangeEventHandler>();
	
	private Integer totalPages;
	private Integer currentPage;
	private Integer progressPercent;
	
	@Override
	public Type<ProgressChangeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ProgressChangeEventHandler handler) {
		handler.onProgressChange(this);		
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
		return progressPercent;
	}

	public void setProgressPercent(Integer progressPercent) {
		this.progressPercent = progressPercent;
	}

	
	
}