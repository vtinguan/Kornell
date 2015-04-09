package kornell.core.scorm12.rte;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class KNL extends DMElement {
	public KNL() {
		super("knl");
		add(first_launch);
	}

	public static DMElement first_launch = new DMElement("first_launch") {
		public Map<String, String> initializeMap(Map<String, String> entries) {
			return defaultTo(entries, defaultValue());
		}

		private String defaultValue() {
			return sdf.format(new Date());
		}
	};
	
	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd hh:mm:ss");
	
	public static KNL dme = new KNL();
}
