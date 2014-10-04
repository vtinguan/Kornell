package kornell.core.to.coursedetails;

@Deprecated
public class InfoTO {
	String type;
	String text;
	public InfoTO(String type, String text) {
		this.type = type;
		this.text = text;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	@Override
	public String toString() {
		return "type: " + type + "  -  " + "text: " + text;
	}
}
