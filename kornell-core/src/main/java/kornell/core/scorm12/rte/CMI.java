package kornell.core.scorm12.rte;

public class CMI extends DMElement {
	public static final CMI dme = new CMI();

	private CMI() {
		super("cmi");
		add(Core.dme);
		add(StudentData.dme);
	}
}
