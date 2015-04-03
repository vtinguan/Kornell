package kornell.core.scorm12;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kornell.core.scorm12.DataType.*;
import static kornell.core.scorm12.SCOAccess.*;

public enum DataModel {
	cmi(null),
	  core(cmi),
	  	student_id, 
	  	student_name, 
	  	lesson_location, 
	  	credit, 
	  	
	  	lesson_status(core,CMIVocabulary("passed","completed","failed","incomplete","browsed","not attempted"), RO,true){
			@Override
			protected Map<String, String> launchMap(Map<String, String> entries) {
				Map<String, String> result = new HashMap<>();
				/* TODO
				*If cmi.core.credit is set to “credit” and there is a mastery score in the manifest 
				* (adlcp:masteryscore), the LMS can change the status to either passed or failed 
				* depending on the student's score compared to the mastery score.
				*/
				
				/* TODO
				 * If there is no mastery score in the manifest (adlcp:masteryscore), the LMS 
				 *  cannot override SCO determined status. 
				 */
				 
			    /*
				* If the student is taking the SCO for no-credit, there is no change to the 
				* lesson_status, with one exception.  If the lesson_mode is "browse", the 
				* lesson_status may change to "browsed" even if the cmi.core.credit is set to no-
				* credit.
				*/
				return result;
				
			}
		},
	  	
	  	entry, 
	  	score, 
	  	total_time, 
	  	lesson_mode, 
	  	exit,
	  	session_time;
	    
	
	
	private DataModel parent = null;
	private List<DataModel> children = new ArrayList<DataModel>();
	
	private DataModel() {
		this(null,null,null,false);
	}
	
	private DataModel(DataModel parent) {
		this(parent,null,null,false);
	}
	
	private DataModel(DataModel parent,DataType type, SCOAccess access, boolean mandatory){
		this.parent = parent;
		this.parent.children.add(this);
	}
	
	public List<DataModel> getChildren() {
		return children;
	}
	
	/**
	 * The launch value is the value to be set to a given data model entry on SCO launch
	 * with the existing context of entries.
	 */
	protected Map<String, String> launchMap(Map<String, String> entries){
		return Collections.emptyMap();
	}
	
	public static Map<String, String> onLaunch(Map<String, String> entries){
		HashMap<String, String> result = new HashMap<>(entries);
		for (DataModel dataModel : values()) {
			result.putAll(dataModel.launchMap(result));
		}
		return result;
	}
	
}
