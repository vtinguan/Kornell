package kornell.gui.client.presentation.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kornell.gui.client.KornellConstants;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SimpleDatePicker extends FlowPanel{
	
	private static KornellConstants constants = GWT.create(KornellConstants.class);
	
    public SimpleDatePicker(){

        final ListBox dropBoxDay = new ListBox(false);
        for (int day = 1; day <= 31; day++){
        	dropBoxDay.addItem(""+day);
        }
        dropBoxDay.ensureDebugId("dropBoxDay");
        dropBoxDay.setWidth("60px");
        this.add(dropBoxDay);

        final ListBox dropBoxMonth = new ListBox(false);
        for (Month month : getMonthList()){
        	dropBoxMonth.addItem(month.getName());
        }
        dropBoxMonth.ensureDebugId("dropBoxMonth");
        dropBoxMonth.setWidth("120px");
        this.add(dropBoxMonth);

        final ListBox dropBoxYear = new ListBox(false);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = currentYear - 100; year < currentYear - 10; year++){
        	dropBoxYear.addItem(""+year);
        }
        dropBoxYear.ensureDebugId("dropBoxYear");
        dropBoxYear.setWidth("80px");
        this.add(dropBoxYear);
        
        this.setVisible(true);
    }
    
    private List<Month> getMonthList(){
    	List<Month> months = new ArrayList<Month>();
    	months.add(new Month(constants.january(), 31));
    	months.add(new Month(constants.february(), 28));
    	months.add(new Month(constants.march(), 31));
    	months.add(new Month(constants.april(), 30));
    	months.add(new Month(constants.may(), 31));
    	months.add(new Month(constants.june(), 30));
    	months.add(new Month(constants.july(), 31));
    	months.add(new Month(constants.august(), 31));
    	months.add(new Month(constants.september(), 30));
    	months.add(new Month(constants.october(), 31));
    	months.add(new Month(constants.november(), 30));
    	months.add(new Month(constants.december(), 31));
    	return months;
    }
    
    class Month{
    	String name;
    	int days;
    	public Month(String name, int days){
    		this.name = name;
    		this.days = days;
    	}
		public int getDays() {
			return days;
		}
		public void setDays(int days) {
			this.days = days;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
    }
}
