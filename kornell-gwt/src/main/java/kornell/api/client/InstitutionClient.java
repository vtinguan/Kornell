package kornell.api.client;

import kornell.core.entity.Institution;

public class InstitutionClient extends RESTClient {

	private String institutionUUID;

	public InstitutionClient(String uuid) {
		this.institutionUUID = uuid;
	}

	public void acceptTerms(Callback<Void> cb) {
		PUT("/institutions/" + institutionUUID + "/acceptTerms").go(cb);
	}

	public void get(Callback<Institution> cb) {
		GET("/institutions/" + institutionUUID).sendRequest(null, cb);
	}

	public void update(Institution institution, Callback<Institution> cb) {
		PUT("/institutions/" + institutionUUID).withContentType(Institution.TYPE).withEntityBody(institution).go(cb);
	}
	
}
