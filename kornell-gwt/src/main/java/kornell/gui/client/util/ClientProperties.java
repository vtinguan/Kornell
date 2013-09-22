package kornell.gui.client.util;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Cookies;

public class ClientProperties {
	private static Storage localStorage = Storage.getLocalStorageIfSupported();
	
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

	public static native String base64Encode(String plain) /*-{
	  return window.btoa(plain);
	}-*/;

	public static native String base64Decode(String base64) /*-{
	  return window.atob(base64);
	}-*/;
}
