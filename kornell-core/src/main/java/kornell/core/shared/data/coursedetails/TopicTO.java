package kornell.core.shared.data.coursedetails;

public class TopicTO {
	String index;
	String title;
	String status;
	String time;
	Integer forumComments;
	Integer newForumComments;
	boolean notes;
	String type; //finished,finishedTest,current,toStart,toStartTest
	
	public TopicTO(String index, String title, String status, String time, Integer forumComments, Integer newForumComments, boolean notes, String type) {
		this.index = index;
		this.title = title;
		this.status = status;
		this.time = time;
		this.forumComments = forumComments;
		this.newForumComments = newForumComments;
		this.notes = notes;
		this.type = type;
	}
	
	public TopicTO(String index, String title, String type) {
		this.index = index;
		this.title = title;
		this.status = "";
		this.time = "";
		this.forumComments = 0;
		this.newForumComments = 0;
		this.notes = false;
		this.type = type;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public Integer getForumComments() {
		return forumComments;
	}
	public void setForumComments(Integer forumComments) {
		this.forumComments = forumComments;
	}
	public Integer getNewForumComments() {
		return newForumComments;
	}
	public void setNewForumComments(Integer newForumComments) {
		this.newForumComments = newForumComments;
	}
	public boolean isNotes() {
		return notes;
	}
	public void setNotes(boolean notes) {
		this.notes = notes;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "index: " + index + "  -  " + "title: " + title + "  -  " + "type: " + type;
	}
	
}
