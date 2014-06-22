package kornell.api.client;

import com.google.gwt.i18n.shared.DateTimeFormat;

public class ClientTime {
	static final DateTimeFormat ISO_8601 = DateTimeFormat
			.getFormat(DateTimeFormat.PredefinedFormat.ISO_8601);

	public static String now() {
		return ISO_8601.format(new java.util.Date());
	}
}
