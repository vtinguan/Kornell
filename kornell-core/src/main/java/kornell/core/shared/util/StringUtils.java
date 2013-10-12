package kornell.core.shared.util;

public class StringUtils {
	public  static String composeURL(String baseURL,String... path) {
		StringBuffer buf = new StringBuffer();
		if(baseURL.endsWith("/"))
			buf.append(baseURL.substring(0,baseURL.length()-1));
		else 
			buf.append(baseURL); 
		for (String segment : path) {
			if (!segment.startsWith("/"))
				buf.append("/");
			buf.append(segment);
		}
		return buf.toString();
	}
}
