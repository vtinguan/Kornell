package kornell.core.scorm12.rte;

import kornell.core.scorm12.rte.knl.FirstLaunch;


public class KNL extends DMElement {
	public KNL() {
		super("knl");
		addAll(FirstLaunch.dme);
	}

	public static KNL dme = new KNL();
}
