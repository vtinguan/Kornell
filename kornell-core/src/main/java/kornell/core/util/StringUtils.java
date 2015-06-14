package kornell.core.util; 

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringUtils {
	
	private static final Sha1 sha1 = new Sha1();

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

	public static URLBuilder with(String base) {
		return new URLBuilder(base);
	}

	/**
	 * Concatenates segments into a single-slashed url or path
	 */
	public static String mkurl(String base, String... path) {
		return composeURL(base, path);
	}

	/**
	 * @deprecated Prefer mkurl()
	 */
	@Deprecated
	public static String composeURL(String base, String... path) {
		StringBuffer buf = new StringBuffer();
		List<String> tokens = new ArrayList<String>();
		if (isSome(base))
			buf.append(removeTrailingSlashes(base));
		if (path != null)
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
		while (isSome(segment) && segment.endsWith("/"))
			segment = segment.substring(0, segment.length() - 1);
		return segment;
	}

	public static final boolean isNone(String str) {
		return str == null || "".equals(str);
	}

	public static final boolean isSome(String str) {
		return !isNone(str);
	}

	public static String digitsOf(String orig) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < orig.length(); i++) {
			char ch = orig.charAt(i);
			if (Character.isDigit(ch))
				buf.append(ch);
		}
		return buf.toString();
	}

	public static OptionString opt(String str) {
		if (isSome(str))
			return new SomeString(str);
		else
			return new NoneString();
	}

	public interface OptionString {
		OptionString orElse(String other);

		String getOrNull();
	}

	public static class SomeString implements OptionString {
		String str;

		public SomeString(String str) {
			this.str = str;
		}

		@Override
		public OptionString orElse(String other) {
			return this;
		}

		@Override
		public String getOrNull() {
			return str;
		}
	}

	public static class NoneString implements OptionString {

		@Override
		public OptionString orElse(String other) {
			return opt(other);
		}

		@Override
		public String getOrNull() {
			return null;
		}

	}

	public static boolean noneEmpty(Object... objs) {
		for (Object obj : objs) {
			if (obj == null || obj.toString().length() == 0)
				return false;
		}
		return true;
	}

	private static String i2s(int i) {
		return (i < 10 ? "0" : "") + i;
	}

	@SuppressWarnings("deprecation")
	public static String isoNow() {
		Date date = new Date();
		String year = i2s(date.getYear());
		String month = i2s(date.getMonth());
		String day = i2s(date.getDate());
		String hour = i2s(date.getHours());
		String min = i2s(date.getMinutes());
		String sec = i2s(date.getSeconds());
		return year + "-" + month + "-" + day + "T" + hour + ":" + min + ":"
				+ sec;
	}

	private static final String HASH_SEP = "|#|";
	public static String hash(String... args) {
		StringBuilder builder = new StringBuilder();
		for (String arg : args) {
			builder.append(arg);
			builder.append(HASH_SEP);
		}
		return sha1.hex_sha1(builder.toString());
	}

}
