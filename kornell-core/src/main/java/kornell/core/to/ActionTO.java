package kornell.core.to;

import kornell.core.scorm.scorm12.rte.action.OpenSCO12Action;

public interface ActionTO {
	public static final String TYPE = TOFactory.PREFIX + "action+json";
	
	ActionType getType();
	void setType(ActionType type);
	
	OpenSCO12Action getOpenSCO12Action();
	void setOpenSCO12Action(OpenSCO12Action action);
}
