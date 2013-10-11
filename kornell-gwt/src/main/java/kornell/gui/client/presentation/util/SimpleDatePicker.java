package kornell.gui.client.presentation.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kornell.gui.client.KornellConstants;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlowPanel;

public class SimpleDatePicker extends FlowPanel{
	
	private static KornellConstants constants = GWT.create(KornellConstants.class);
	private ListBox dropBoxDay, dropBoxMonth, dropBoxYear;
	
    public SimpleDatePicker(){

        dropBoxDay = new ListBox(false);
        for (int day = 1; day <= 31; day++){
        	dropBoxDay.addItem(""+day);
        }
        dropBoxDay.ensureDebugId("dropBoxDay");
        dropBoxDay.setWidth("60px");
        this.add(dropBoxDay);

        dropBoxMonth = new ListBox(false);
        dropBoxMonth.addItem("");
        for (String month : getMonthList()){
        	dropBoxMonth.addItem(month);
        }
        dropBoxMonth.ensureDebugId("dropBoxMonth");
        dropBoxMonth.setWidth("120px");
        this.add(dropBoxMonth);

        dropBoxYear = new ListBox(false);
        dropBoxYear.addItem("");
        int currentYear = Integer.parseInt(DateTimeFormat.getFormat("yyyy").format(new Date()));
        for (int year = currentYear - 10; year > currentYear - 100; year--){
        	dropBoxYear.addItem(""+year);
        }
        dropBoxYear.ensureDebugId("dropBoxYear");
        dropBoxYear.setWidth("80px");
        this.add(dropBoxYear);
        
        dropBoxMonth.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				updatePossibleDays();
			}
		});
        
        dropBoxYear.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				updatePossibleDays();
			}
		});
        updatePossibleDays();
        this.setVisible(true);
    }

	private void updatePossibleDays() {
		int maxDays = getMaxDays();
        int index = dropBoxDay.getSelectedIndex();
        dropBoxDay.clear();
    	dropBoxDay.addItem("");
        for (int day = 1; day <= maxDays; day++){
        	dropBoxDay.addItem(""+day);
        }
        dropBoxDay.setSelectedIndex(index > maxDays - 1 ? maxDays - 1 : index);
	}
    
    private int getMaxDays() {
		String month = dropBoxMonth.getValue();
		if(dropBoxMonth.getSelectedIndex() <= 0 ||
				month.equals(constants.january()) ||
				month.equals(constants.march()) ||
				month.equals(constants.may()) ||
				month.equals(constants.july()) ||
				month.equals(constants.august()) ||
				month.equals(constants.october()) ||
				month.equals(constants.december())){
			return 31;
		} else if(month.equals(constants.february())){
			int year = Integer.parseInt(dropBoxYear.getValue());
			if(year % 4 == 0){
				return 29;
			} else {
				return 28;
			}
		} else {
			return 30;
		}
	}

	private List<String> getMonthList(){
    	List<String> months = new ArrayList<String>();
    	months.add(constants.january());
    	months.add(constants.february());
    	months.add(constants.march());
    	months.add(constants.april());
    	months.add(constants.may());
    	months.add(constants.june());
    	months.add(constants.july());
    	months.add(constants.august());
    	months.add(constants.september());
    	months.add(constants.october());
    	months.add(constants.november());
    	months.add(constants.december());
    	return months;
    }
	
	public boolean isSelected(){
		return dropBoxDay.getSelectedIndex() > 0 && 
				dropBoxMonth.getSelectedIndex() > 0 &&
				dropBoxYear.getSelectedIndex() > 0;
	}
}
