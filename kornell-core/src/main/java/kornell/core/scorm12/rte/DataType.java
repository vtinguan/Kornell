package kornell.core.scorm12.rte;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DataType {
	static DataType CMIString255 = new DataType(){
		protected boolean isInstance(String value) {
			return value == null || value.length() <= 255;
		};
	};
	
	static DataType CMIDecimal = new DataType(){
		public boolean isInstance(String value) {
			try{
				Integer.parseInt(value);
				return true;
			}catch(Exception e){
				return false;
			}
		}
	};
	
	Set<String> words = new HashSet<>();
	public DataType(String... words) {
		this.words.addAll(Arrays.asList(words));
	}
	
	public static DataType CMIVocabulary(String... words) {		
		return new DataType(words);
	}
	
	public boolean check(String value){
		boolean isVocab = !words.isEmpty();
		boolean valueNotDeclared = !words.contains(value);
		if (isVocab && valueNotDeclared){
			return false;
		}
		else return isInstance(value);
	}

	protected boolean isInstance(String value) {
		return true;
	}
}
