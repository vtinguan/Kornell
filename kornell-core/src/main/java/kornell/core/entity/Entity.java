package kornell.core.entity;

import java.io.Serializable;

public interface Entity extends Serializable {
	String getUUID();
	void setUUID(String UUID);
}
