package kornell.core.entity;



public class PersonCategory {

	public static String getSexSuffix(Person person) {
		if("F".equals(person.getSex()))
			return "a";
		else if("M".equals(person.getSex()))
			return "o";
		else
			return "o(a)";
	}
	
}
