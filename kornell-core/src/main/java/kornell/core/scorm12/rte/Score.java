package kornell.core.scorm12.rte;

public class Score extends DMElement {
	public static final Score dme = new Score();
	public static final Raw raw = Raw.dme; 
	
	public Score() {
		super("score");
		add(raw);
	}
}
