package kornell.gui.client.util.forms.formfield;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlowPanel;

import kornell.gui.client.KornellConstants;

public class SimpleDatePicker extends FlowPanel {

	private static KornellConstants constants = GWT.create(KornellConstants.class);
	private ListBox dropBoxDay, dropBoxMonth, dropBoxYear;

	public SimpleDatePicker() {
		this(null);
		this.addStyleName("datePicker");
	}

	public SimpleDatePicker(Date birthDate) {

		dropBoxDay = new ListBox(false);
		for (int day = 1; day <= 31; day++) {
			dropBoxDay.addItem("" + day);
		}
		dropBoxDay.ensureDebugId("dropBoxDay");
		dropBoxDay.addStyleName("dropBoxDay");
		this.add(dropBoxDay);

		dropBoxMonth = new ListBox(false);
		dropBoxMonth.addItem("");
		List<String> months = getMonthList();
		for (int i = 0; i < months.size(); i++) {
			dropBoxMonth.addItem(months.get(i), "" + (i + 1));
		}
		dropBoxMonth.ensureDebugId("dropBoxMonth");
		dropBoxMonth.addStyleName("dropBoxMonth");
		this.add(dropBoxMonth);

		dropBoxYear = new ListBox(false);
		dropBoxYear.addItem("");
		int currentYear = Integer.parseInt(DateTimeFormat.getFormat("yyyy").format(new Date()));
		for (int year = currentYear - 10; year > currentYear - 100; year--) {
			dropBoxYear.addItem("" + year);
		}
		dropBoxYear.ensureDebugId("dropBoxYear");
		dropBoxYear.addStyleName("dropBoxYear");
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

		setFields(birthDate);
		updatePossibleDays();
		this.setVisible(true);
	}

	@SuppressWarnings("deprecation")
	public void setFields(Date birthDate) {
		if (birthDate != null) {
			dropBoxDay.setSelectedValue(""+birthDate.getDate());
			dropBoxMonth.setSelectedValue(""+(birthDate.getMonth()+1));
			dropBoxYear.setSelectedValue(""+(birthDate.getYear()+1900));
		}
	}


	private void updatePossibleDays() {
		int maxDays = getMaxDays();
		int index = dropBoxDay.getSelectedIndex();
		dropBoxDay.clear();
		dropBoxDay.addItem("");
		for (int day = 1; day <= maxDays; day++) {
			dropBoxDay.addItem("" + day);
		}
		dropBoxDay.setSelectedIndex(index > maxDays ? maxDays : index);
	}

	private int getMaxDays() {
		String month = dropBoxMonth.getValue();
		if (dropBoxMonth.getSelectedIndex() <= 0 || month.equals(constants.january()) 
				|| month.equals(constants.march()) || month.equals(constants.may())
		    || month.equals(constants.july()) || month.equals(constants.august()) 
		    || month.equals(constants.october()) || month.equals(constants.december())) {
			return 31;
		} else if (month.equals(constants.february())) {
			int year = Integer.parseInt(dropBoxYear.getValue());
			if (year % 4 == 0) {
				return 29;
			} else {
				return 28;
			}
		} else {
			return 30;
		}
	}

	private List<String> getMonthList() {
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

	public boolean isSelected() {
		return dropBoxDay.getSelectedIndex() > 0 && dropBoxMonth.getSelectedIndex() > 0 && dropBoxYear.getSelectedIndex() > 0;
	}

	public String getDisplayText() {
		if (!isSelected())
			return null;
		String day = dropBoxDay.getValue();
		day = day.length() == 1 ? "0" + day : day;
		String month = dropBoxMonth.getItemText(dropBoxMonth.getSelectedIndex());
		return day + "/" + month + "/" + dropBoxYear.getValue();
	}

	public String getPersistText() {
		if (!isSelected())
			return null;
		String day = dropBoxDay.getValue();
		day = day.length() == 1 ? "0" + day : day;
		String month = "" + dropBoxMonth.getSelectedIndex();
		month = month.length() == 1 ? "0" + month : month;
		return dropBoxYear.getValue() + "-" + month + "-" + day;
	}
}
