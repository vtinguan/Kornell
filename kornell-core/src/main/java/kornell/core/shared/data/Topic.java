package kornell.core.shared.data;

public interface Topic extends Node {
	public static String TYPE = "application/vnd.kornell.v1.topic+json";

	String getName();
	void setName(String name);

}
