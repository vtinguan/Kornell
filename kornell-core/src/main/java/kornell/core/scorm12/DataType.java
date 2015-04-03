package kornell.core.scorm12;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DataType {
	static DataType CMIString255 = new DataType();
	
	Set<String> words = new HashSet<>();
	public DataType(String... words) {
		this.words.addAll(Arrays.asList(words));
	}
	
	public static DataType CMIVocabulary(String... words) {		
		return new DataType(words);
	}
}
