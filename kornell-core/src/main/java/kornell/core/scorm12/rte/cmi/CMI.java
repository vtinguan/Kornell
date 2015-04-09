package kornell.core.scorm12.rte.cmi;

import kornell.core.scorm12.rte.DMElement;

public class CMI extends DMElement {
	public static final CMI dme = new CMI();

	private CMI() {
		super("cmi");
		addAll(Core.dme,
				StudentData.dme,
				SuspendData.dme);
	}
}
