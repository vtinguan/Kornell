package kornell.core.entity;

import java.util.Date;

public interface Registration {
	public static String TYPE = EntityFactory.PREFIX + "registration+json";
	
	String getPersonUUID();
	void setPersonUUID(String UUID);
	
	String getInstitutionUUID();
	void setInstitutionUUID(String UUID);
	
	//TODO: Enhance security with Digital Signatures
	Date getTermsAcceptedOn();
	void setTermsAcceptedOn(Date termsAcceptedOn);
}
