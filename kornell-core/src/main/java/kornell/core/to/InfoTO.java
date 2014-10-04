package kornell.core.to;

public interface InfoTO {
	String getUUID();
	void setUUID(String UUID);
	
	String getCourseVersionUUID();
	void setCourseVersionUUID(String courseVersionUUID);
	
	String getCategory();
	void setCategory(String category);

	String getSubCategory();
	void setSubCategory(String subCategory);

	Integer getSequence();
	void setSequence(Integer sequence);

	String getTitle();
	void setTitle(String title);

	String getText();
	void setText(String text);

}
