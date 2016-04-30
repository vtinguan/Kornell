package kornell.core.scorm12.rte.knl;

import kornell.core.scorm12.rte.DMElement;
import kornell.core.util.StringUtils;

import static kornell.core.scorm12.rte.DataType.*;
import static kornell.core.scorm12.rte.SCOAccess.*;

import java.util.Map;

import kornell.core.entity.CourseClass;
import kornell.core.entity.Enrollment;
import kornell.core.entity.Person;

public class ClassStartDate extends DMElement {
	public static final ClassStartDate dme = new ClassStartDate();

	public ClassStartDate() {
		super("class_start_date", CMIString255, RO);
	}
	
	@Override
	public Map<String, String> initializeMap(Map<String, String> entries, 
			Person person, 
			Enrollment enrollment,
			CourseClass courseClass) {
		return set(entries,StringUtils.toISO(courseClass.getStartDate()));
	}
}
