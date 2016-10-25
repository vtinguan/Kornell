package kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.edit;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.Pagination;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class WizardSlideItemQuizQuestionNavigator extends Pagination{
	
	private int maxPaginationTabs = 10;

	public WizardSlideItemQuizQuestionNavigator() {
	}

	public void updatePagination(int pageSize, int pageNumber, int totalRowCount) {
		clear();
    	int navLinkCount = (totalRowCount+pageSize-1)/pageSize;
    	setVisible(navLinkCount > 1);
    	setAlignment("centered");
    	
    	if(navLinkCount < maxPaginationTabs){
    		for(int i = 1; i <= navLinkCount; i++){
    			add(createNavLink(i, i==pageNumber));
    		}
    	} else if(pageNumber <= (maxPaginationTabs / 2)){
    		for(int i = 1; i <= maxPaginationTabs; i++){
    			add(createNavLink(i, i==pageNumber));
    		}
    		add(createNavLink(">", navLinkCount));
    	} else if(pageNumber > navLinkCount - (maxPaginationTabs / 2)){
    		add(createNavLink("<", 1));
    		for(int i = (navLinkCount - maxPaginationTabs) + 1; i <= navLinkCount; i++){
    			add(createNavLink(i, i==pageNumber));
    		}
    	} else {
    		add(createNavLink("<", 1));
    		for(int i = pageNumber - (maxPaginationTabs / 2); i <= pageNumber + (maxPaginationTabs / 2); i++){
    			add(createNavLink(i, i==pageNumber));
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
					//@TODO
					//set pageNumber and change the question
					//IconAnchor thisThing = (IconAnchor) event.getSource();
					//thisThing.getName().trim()
				}
			});
		}
		return navLink;
	}
	
}
