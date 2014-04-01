package kornell.core.value;

import com.google.web.bindery.autobean.shared.AutoBean;

public class DateCategory {
	@SuppressWarnings("deprecation")
	public static java.util.Date toJUD(AutoBean<Date> datebean) {
		Date date = datebean.as();
	    java.util.Date jud = new java.util.Date(0);
	    jud.setYear(date.getYear());
	    jud.setMonth(date.getMonth());
	    jud.setDate(date.getDay());
	    return jud;
	}
	
	public static Date fromJUD(AutoBean<Date> datebean, java.util.Date jud) {
		Date date = datebean.as();
		date.setDay(jud.getDate());
		date.setMonth(jud.getMonth());
		date.setYear(jud.getYear());
	    return date;
	}
	
	public static String toString(AutoBean<Date> datebean) {
		Date date = datebean.as();
		return padded(date.getYear()) + "-"+ padded(date.getMonth()) + "-" + padded(date.getDay());
	}
	
	public static String padded(int x){
		return (x < 10 ? "0" : "") + x;
	}
}
