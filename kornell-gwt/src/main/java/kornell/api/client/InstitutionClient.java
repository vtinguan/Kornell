package kornell.api.client;

import kornell.core.entity.Institution;
import kornell.core.to.InstitutionRegistrationPrefixesTO;

public class InstitutionClient extends RESTClient {

	private String institutionUUID;

	public InstitutionClient(String uuid) {
		this.institutionUUID = uuid;
	}

	public void get(Callback<Institution> cb) {
		GET("/institutions/" + institutionUUID).sendRequest(null, cb);
	}

	public void update(Institution institution, Callback<Institution> cb) {
		PUT("/institutions/" + institutionUUID).withContentType(Institution.TYPE).withEntityBody(institution).go(cb);
	}

	public void getRegistrationPrefixes(Callback<InstitutionRegistrationPrefixesTO> cb) {
		GET("/institutions/" + institutionUUID + "/registrationPrefixes").sendRequest(null, cb);
	}

	
}
