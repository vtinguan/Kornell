package kornell.core.scorm12.rte;

import kornell.core.scorm12.rte.knl.FirstLaunch;
import kornell.core.scorm12.rte.knl.LaunchDate;


public class KNL extends DMElement {
	public KNL() {
		super("knl");
		addAll(FirstLaunch.dme,
				LaunchDate.dme);
	}

	public static KNL dme = new KNL();
}
