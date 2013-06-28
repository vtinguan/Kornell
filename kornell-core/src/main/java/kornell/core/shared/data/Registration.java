package kornell.core.shared.data;

import java.util.Date;

public interface Registration {
	String getPersonUUID();
	void setPersonUUID(String UUID);
	
	String getInstitutionUUID();
	void setInstitutionUUID(String UUID);
	
	//TODO: Enhance security with Digital Signatures
	Date getTermsAcceptedOn();
	void setTermsAcceptedon(Date termsAcceptedOn);
}
