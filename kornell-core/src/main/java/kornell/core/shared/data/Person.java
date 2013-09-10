package kornell.core.shared.data;

public interface Person {
	String getUUID();
	void setUUID(String uuid);
	String getFullName();
	void setFullName(String fullName);
	String getLastPlaceVisited();
	void setLastPlaceVisited(String lastPlaceVisited);
}
