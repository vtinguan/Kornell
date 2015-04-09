package kornell.core.scorm12.rte;

import static kornell.core.scorm12.rte.DataType.*;
import static kornell.core.scorm12.rte.SCOAccess.*;
import static kornell.core.util.StringUtils.*;

public class MasteryScore extends DMElement {
	public static final MasteryScore dme = new MasteryScore();

	public MasteryScore() {
		super("mastery_score",false,CMIDecimal,RO);
	}
}
