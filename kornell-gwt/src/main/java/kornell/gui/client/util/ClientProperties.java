package kornell.gui.client.util;

import kornell.gui.client.personnel.Dean;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Cookies;

//TODO: if this is user specific, move to UserSession
public class ClientProperties {
	private static Storage localStorage = Storage.getLocalStorageIfSupported();

	public static final String SEPARATOR = ".";
	public static final String PREFIX = "Kornell.v1.";
	public static final String X_KNL_TOKEN = "X-KNL-TOKEN";
	public static final String SELECTED_COURSE_CLASS = "SELECTED_COURSE_CLASS";
	
	public static String get(String propertyName){
		if(localStorage != null){
			return localStorage.getItem(propertyName);
		} else if (Cookies.isCookieEnabled()) {
			return Cookies.getCookie(propertyName);
		}
		return null;
	}
	
	public static void set(String propertyName, String propertyValue){
		if(localStorage != null){
			localStorage.setItem(propertyName, propertyValue);
		} else if (Cookies.isCookieEnabled()) {
			Cookies.setCookie(propertyName, propertyValue);
		}
	}
	
	public static void remove(String propertyName){
		if(localStorage != null){
			localStorage.removeItem(propertyName);
		} else if (Cookies.isCookieEnabled()) {
			Cookies.removeCookie(propertyName);
		}
	}
	
	public static String getDecoded(String propertyName){
		String value = get(propertyName);
		return value == null ? null : base64Decode(value);
	}
	
	public static void setEncoded(String propertyName, String propertyValue){
		set(propertyName, base64Encode(propertyValue));
	}

	public static String base64Encode(String plain) {
		return Base64Utils.toBase64(plain.getBytes());
	};

	public static String base64Decode(String base64) {
		return new String(Base64Utils.fromBase64(base64));
	}


}
