package kornell.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.http.client.UrlBuilder;

public class StringUtils {

	public static class URLBuilder {
		private String base;
		private String[] segments;
		private Map<String, String> params = new HashMap<String, String>();

		public URLBuilder(String base) {
			this.base = base;
		}

		public URLBuilder withPath(String... segments) {
			this.segments = segments;
			return this;
		}

		public URLBuilder withParam(String key, String value) {
			params.put(key, value);
			return this;
		}

		public String build() {
			StringBuilder url = new StringBuilder();
			url.append(composeURL(base, segments));
			url.toString();
			if (!params.isEmpty()) {
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

	public static URLBuilder url(String base) {
		return new URLBuilder(base);
	}

	public static String composeURL(String base,String... path) {		
		StringBuffer buf = new StringBuffer();
		List<String> tokens = new ArrayList<String>();
		if(isSome(base))
			buf.append(removeTrailingSlashes(base));
		if(path != null)
			tokens.addAll(Arrays.asList(path));
		for (String segment : tokens) {			
			if (isSome(segment)) {
				if (!segment.startsWith("/"))
					buf.append("/");
				segment = removeTrailingSlashes(segment);
				buf.append(segment);
			}
		}
		return buf.toString();
	}

	private static String removeTrailingSlashes(String segment) {
		while(isSome(segment) && segment.endsWith("/"))
			segment = segment.substring(0,segment.length()-1);
		return segment;
	}

	public static final boolean isNone(String str) {
		return str == null || "".equals(str);
	}

	public static final boolean isSome(String str) {
		return !isNone(str);
	}

}
