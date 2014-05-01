package kornell.core.entity;

//TODO: Persist transition-triggering events
//TODO: Document states and transitions
public enum EnrollmentState {	
	notEnrolled, //??? 
	preEnrolled, //Pre-registered by institution but not activated yet
	enrolled,    //Course has been started
	requested,   //Requested participation in a private course
	denied,      //Participation request denied 
	cancelled,   //Participation canceled by institution (payment, timeout, ?)
	finished     //All content seen and all required evaluations either passed or failed 
	//TODO: Abandoned?
}	
