package kornell.core.util;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.http.client.UrlBuilder;

public class StringUtils {
	
	public static class URLBuilder{
		private String base;
		private String[] segments;	
		private Map<String,String> params = new HashMap<String, String>();
		
		
		public URLBuilder(String base){
			this.base = base;
		}
		
		public URLBuilder withPath(String... segments){
			this.segments = segments;
			return this;
		}
		
		public URLBuilder withParam(String key, String value){
			params.put(key, value);
			return this;
		}
		
		public String build() {
			StringBuilder url = new StringBuilder();
			url.append(composeURL(base, segments));
			url.toString();
			if(! params.isEmpty()){
				url.append("?");
				for (Map.Entry<String, String> entry : params.entrySet()) {
					url.append(entry.getKey());
					url.append("=");
					url.append(entry.getValue());
				}
			}
			return url.toString();
		}
	}
	
	public static URLBuilder url(String base){
		return new URLBuilder(base);
	}
	
	public  static String composeURL(String baseURL,String... path) {
		StringBuffer buf = new StringBuffer();
		if(baseURL.endsWith("/"))
			buf.append(baseURL.substring(0,baseURL.length()-1));
		else 
			buf.append(baseURL); 
		if(path != null) for (String segment : path) {
			if (!(segment.startsWith("/") || segment.isEmpty()))
				buf.append("/");
			buf.append(segment);
		}
		return buf.toString();
	}
}
