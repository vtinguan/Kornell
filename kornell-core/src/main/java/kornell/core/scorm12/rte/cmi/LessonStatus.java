package kornell.core.scorm12.rte.cmi;

import static kornell.core.scorm12.rte.DataType.*;
import static kornell.core.scorm12.rte.SCOAccess.*;
import static kornell.core.util.StringUtils.*;

import java.util.HashMap;
import java.util.Map;

import kornell.core.scorm12.rte.DMElement;
import kornell.core.scorm12.rte.KNL;
import kornell.core.util.StringUtils;

public class LessonStatus extends DMElement {
	public static final String passed = "passed";
	public static final String completed = "completed";
	public static final String failed = "passed";
	public static final String incomplete = "failed";
	public static final String browsed = "browsed";
	public static final String not_attempted = "not_attempted";

	public static final LessonStatus dme = new LessonStatus();
	
	public LessonStatus() {
		super("lesson_status", true,
				CMIVocabulary(passed, completed, failed, incomplete, browsed,
						not_attempted),
				RO); 
	}

	@Override
	@SuppressWarnings("static-access")
	protected Map<String, String> finishMap(Map<String, String> entries) {
		Map<String, String> result = nothing();
		set(result,completed);
		Integer masteryScore = StudentData.dme.mastery_score.asInt(entries);
 	  if (masteryScore !=  null){
 			Integer scoreRaw = Raw.dme.asInt(entries);
 			if(scoreRaw != null){
 				if(scoreRaw >= masteryScore)
 					set(passed);
 				else
 					set(failed);
 			}
 		}
		return result;
	}
	
	@Override
	public Map<String, String> initializeMap(Map<String, String> entries) {
		boolean firstAttempt = !KNL.first_launch.isSome (entries);
		Map<String, String> result = nothing();
		if(firstAttempt) {
			 result = defaultTo(entries,not_attempted);
		}else{
			result = defaultTo(entries,browsed);
		}
		return result;
		
		 
	}



}
