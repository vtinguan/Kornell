package kornell.core.value;

public interface Date {
	int getDay();
	void setDay(int day);

	int getMonth();
	void setMonth(int month);

	int getYear();
	void setYear(int year);
	
	int getHour();
	void setHour(int hour);
	
	int getMinutes();
	void setMinutes(int minutes);
	
	int getSeconds();
	void setSeconds(int seconds);
	
	String getTimezone();
	void setTimezone(String timezone);
}