package kornell.api.client;

import kornell.core.entity.CourseClass;
import kornell.core.entity.Institution;
import kornell.core.entity.Roles;
import kornell.core.to.InstitutionEmailWhitelistTO;
import kornell.core.to.InstitutionHostNamesTO;
import kornell.core.to.InstitutionRegistrationPrefixesTO;
import kornell.core.to.RolesTO;

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

	public void getAdmins(String bindMode, Callback<RolesTO> cb) {
		GET("institutions",institutionUUID,"admins"+"?bind="+bindMode).withContentType(CourseClass.TYPE).go(cb);
	}

	public void updateAdmins(Roles roles, Callback<Roles> cb) {
		PUT("institutions",institutionUUID,"admins").withContentType(Roles.TYPE).withEntityBody(roles).go(cb);
	}

	public void getHostnames(Callback<InstitutionHostNamesTO> cb) {
		GET("institutions",institutionUUID,"hostnames").go(cb);
	}

	public void updateHostnames(InstitutionHostNamesTO institutionHostNamesTO, Callback<InstitutionHostNamesTO> cb) {
		PUT("institutions",institutionUUID,"hostnames").withContentType(InstitutionHostNamesTO.TYPE).withEntityBody(institutionHostNamesTO).go(cb);
	}

	public void getEmailWhitelist(Callback<InstitutionEmailWhitelistTO> cb) {
		GET("institutions",institutionUUID,"emailWhitelist").go(cb);
	}

	public void updateEmailWhitelist(InstitutionEmailWhitelistTO institutionEmailWhitelistTO, Callback<InstitutionEmailWhitelistTO> cb) {
		PUT("institutions",institutionUUID,"emailWhitelist").withContentType(InstitutionEmailWhitelistTO.TYPE).withEntityBody(institutionEmailWhitelistTO).go(cb);
	}
	
	public void getUploadURL(String filename, Callback<String> callback) {
		GET("institutions", institutionUUID, "uploadUrl", filename).go(callback);
	}
}
