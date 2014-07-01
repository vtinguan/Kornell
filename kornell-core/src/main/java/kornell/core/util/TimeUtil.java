package kornell.core.util;

import kornell.core.value.Date;

public class TimeUtil {

	
	@SuppressWarnings("deprecation")
	public static java.util.Date toJUD(Date date) {
		if(date == null) return null;
		java.util.Date jud = new java.util.Date(0);
		jud.setYear(date.getYear());
		jud.setMonth(date.getMonth());
		jud.setDate(date.getDay());
		return jud;
	}

	public static Date fromJUD(Date date, java.util.Date jud) {
		if (date != null && jud != null) {
			date.setDay(jud.getDate());
			date.setMonth(jud.getMonth());
			date.setYear(jud.getYear());
		}
		return date;
	}

	public static String toString(Date date) {
		return padded(date.getYear()) + "-" + padded(date.getMonth()) + "-"
				+ padded(date.getDay());
	}

	private static String padded(int x) {
		return (x < 10 ? "0" : "") + x;
	}
	

}
