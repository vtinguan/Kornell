package kornell.core.to;

public interface LaunchEnrollmentTO {
	public static final String TYPE = TOFactory.PREFIX + "launchenrollment+json";

	ActionTO getActionTO();
	void setActionTO(ActionTO actionTO);
}
