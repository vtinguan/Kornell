package kornell.core.scorm12.rte.cmi;

import kornell.core.scorm12.rte.DMElement;

public class Core extends DMElement {
	public static final Core dme = new Core();
	
	private Core() {
		super("core");
		add(LessonStatus.dme);
		add(Score.dme);
	}
}
