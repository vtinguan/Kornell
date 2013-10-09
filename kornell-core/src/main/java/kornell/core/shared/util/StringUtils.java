package kornell.core.shared.util;

public class StringUtils {
	public  static String composeURL(String baseURL,String... path) {
		StringBuffer buf = new StringBuffer();
		if(baseURL.endsWith("/"))
			baseURL = baseURL.substring(baseURL.length()-1);
		buf.append(baseURL); 
		for (String segment : path) {
			if (!segment.startsWith("/"))
				buf.append("/");
			buf.append(segment);
		}
		return buf.toString();
	}
}
