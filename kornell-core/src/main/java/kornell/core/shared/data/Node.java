package kornell.core.shared.data;

import java.util.List;

public interface Node {
	List<Content> getChildren();
	void setChildren(List<Content> children);
}
