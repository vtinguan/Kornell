package kornell.core.lom;

//TODO: Try removing manual polymorphism
public interface Content {
	ContentFormat getFormat();
	void setFormat(ContentFormat format);
	
	boolean isVisited();
	void setVisited(boolean visited);
	
	ExternalPage getExternalPage();
	void setExternalPage(ExternalPage page);
	
	Topic getTopic();
	void setTopic(Topic topic);
}
