package kornell.core.shared.event;

import java.util.Date;

import kornell.core.shared.data.Actom;
import kornell.core.shared.data.Person;

public interface ActomEntered {
	public static final String TYPE = "application/vnd.kornell.v1.event.ActomEntered+json";

	Date getTime();
	void setTime(Date time);
	
	Person getFrom();
	void setFrom(Person person);
	
	Actom getActom();
	void setActom(Actom actom);
}
