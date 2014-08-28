package kornell.core.scorm.scorm12.cam;

import kornell.core.scorm.scorm12.cam.adlcp.PreRequisites;
import kornell.core.scorm.scorm12.cam.adlcp.TimeLimitAction;


public interface Item extends HasItems,HasMetadata {
	/* IMS */
	String getIdentifier();
	void setIdentifier(String identifier);

	Boolean isVisible();
	void setVisible(Boolean visible);
	
	String getTitle();
	void setTitle(String title);
	
	String getIdentifierRef();
	void setIdentifierRef(String identifierRef);
	
	
	/* ADLCP */
	PreRequisites getPreRequisites();
	void setPreRequisites(PreRequisites preRequisites);
	
	String getMaxTimeAllowed();
	void setMaxTimeAllowed(String maxTimeAllowed);
	
	TimeLimitAction getTimeLimitAction();
	void setTimeLimitAction(TimeLimitAction timeLimitAction);
	
	String getMasteryScore();
	void setMasteryScore(String masteryScore);
}
