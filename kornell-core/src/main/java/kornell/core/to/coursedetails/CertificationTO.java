package kornell.core.to.coursedetails;

public class CertificationTO {
	String type;
	String name;
	String description;
	String status;
	String grade;
	String actions;
	
	public CertificationTO(String type, String name, String description, String status, String grade, String actions) {
		this.type = type;
		this.name = name;
		this.description = description;
		this.status = status;
		this.grade = grade;
		this.actions = actions;
	}
	
	public CertificationTO(String type, String name, String description) {
		this.type = type;
		this.name = name;
		this.description = description;
		this.status = "-";
		this.grade = "";
		this.actions = "";
	}
	
	
	public String getType() {
		return type;
	}
	
	
	public void setType(String type) {
		this.type = type;
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getDescription() {
		return description;
	}
	
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public String getStatus() {
		return status;
	}
	
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	public String getGrade() {
		return grade;
	}
	
	
	public void setGrade(String grade) {
		this.grade = grade;
	}
	
	
	public String getActions() {
		return actions;
	}
	
	
	public void setActions(String actions) {
		this.actions = actions;
	}
	
	
	@Override
	public String toString() {
		return "type: " + type + "  -  " + "name: " + name + "  -  " + "description: " + description;
	}
}
