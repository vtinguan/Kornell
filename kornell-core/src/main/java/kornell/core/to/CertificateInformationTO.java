package kornell.core.to;

import java.util.Date;

public class CertificateInformationTO {
	String personFullName;
	String personCPF;
	String courseTitle;
	String assetsURL;
	String distributionPrefix;
	Date courseClassFinishedDate;
	
	public String getPersonFullName() {
		return personFullName;
	}
	public void setPersonFullName(String personFullName) {
		this.personFullName = personFullName;
	}
	public String getPersonCPF() {
		return personCPF;
	}
	public void setPersonCPF(String personCPF) {
		this.personCPF = personCPF;
	}
	public String getCourseTitle() {
		return courseTitle;
	}
	public void setCourseTitle(String courseTitle) {
		this.courseTitle = courseTitle;
	}
	public String getAssetsURL() {
		return assetsURL;
	}
	public void setAssetsURL(String assetsURL) {
		this.assetsURL = assetsURL;
	}
	public String getDistributionPrefix() {
		return distributionPrefix;
	}
	public void setDistributionPrefix(String distributionPrefix) {
		this.distributionPrefix = distributionPrefix;
	}
	public Date getCourseClassFinishedDate() {
		return courseClassFinishedDate;
	}
	public void setCourseClassFinishedDate(Date courseClassFinishedDate) {
		this.courseClassFinishedDate = courseClassFinishedDate;
	}
	
}
