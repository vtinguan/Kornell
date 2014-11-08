package kornell.api.client;

import kornell.core.to.RegistrationRequestTO;
import kornell.core.to.UserHelloTO;
import kornell.core.to.UserInfoTO;
import kornell.core.util.StringUtils;

import com.google.gwt.http.client.URL;

public class UserClient extends RESTClient {

	// TODO: Is this safe?
	public void getUser(String username, Callback<UserInfoTO> cb) {
		GET("/user/" + username).sendRequest(null, cb);
	}

	public void getUserHello(String name, String hostName, Callback<UserHelloTO> cb) {
		String path = "/user/hello?" + (StringUtils.isSome(name) ? "name="+name : "hostName="+hostName);
		GET(path).sendRequest(null, cb);
	}

	public void checkUser(String email, Callback<UserInfoTO> cb) {
		GET("/user/check/" + email).sendRequest(null, cb);
	}

	public void requestRegistration(RegistrationRequestTO registrationRequestTO, Callback<UserInfoTO> cb) {
		PUT("/user/registrationRequest").withContentType(RegistrationRequestTO.TYPE).withEntityBody(registrationRequestTO).go(cb);
	}

	public void requestPasswordChange(String email, String institutionName, Callback<Void> cb) {
		GET("/user/requestPasswordChange/" + URL.encodePathSegment(email) + "/" + institutionName).sendRequest(null, cb);
	}

	public void changePassword(String password, String passwordChangeUUID, Callback<UserInfoTO> cb) {
		GET("/user/changePassword/" + URL.encodePathSegment(password) + "/" + passwordChangeUUID).sendRequest(null, cb);
	}

	public void changeTargetPassword(String targetPersonUUID, String password, Callback<Void> cb) {
		PUT("/user/changePassword/" + targetPersonUUID + "/?password=" + URL.encodePathSegment(password)).sendRequest(null, cb);
	}

	public void hasPowerOver(String targetPersonUUID, Callback<Boolean> cb) {
		GET("/user/hasPowerOver/" + targetPersonUUID).sendRequest(null, cb);
	}

	public void updateUser(UserInfoTO userInfo, Callback<UserInfoTO> cb) {
		PUT("/user/" + userInfo.getPerson().getUUID()).withContentType(UserInfoTO.TYPE).withEntityBody(userInfo).go(cb);
	}
	
	
}
