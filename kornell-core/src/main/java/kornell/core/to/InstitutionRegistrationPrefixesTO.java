package kornell.core.to;

import java.util.List;

public interface InstitutionRegistrationPrefixesTO {
	public static final String TYPE = TOFactory.PREFIX + "institutionRegistrationPrefixes+json";
		
	List<String> getInstitutionRegistrationPrefixes(); 
	void setInstitutionRegistrationPrefixes(List<String> institutionRegistrationPrefixes);

}
