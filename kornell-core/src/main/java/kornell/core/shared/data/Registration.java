package kornell.core.shared.data;

import java.util.Date;

public interface Registration extends Entity {
	//TODO: Enhance security with Digital Signatures
	Date getTermsAcceptedOn();
	void setTermsAcceptedon(Date termsAcceptedOn);
	

}
