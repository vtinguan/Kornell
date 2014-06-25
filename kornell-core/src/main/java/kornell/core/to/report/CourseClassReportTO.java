package kornell.core.to.report;

import java.util.List;

public class CourseClassReportTO {
	private String fullName;
	private String username;
	private String state;
	private String progressState;
	private Integer progress;
	private EnrollmentsBreakdownTO enrollmentsBreakdownTO;
	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getProgressState() {
		return progressState;
	}
	public void setProgressState(String progressState) {
		this.progressState = progressState;
	}
	public Integer getProgress() {
		return progress;
	}
	public void setProgress(Integer progress) {
		this.progress = progress;
	}
	public EnrollmentsBreakdownTO getEnrollmentsBreakdownTO() {
		return enrollmentsBreakdownTO;
	}
	public void setEnrollmentsBreakdownTO(EnrollmentsBreakdownTO enrollmentsBreakdownTO) {
		this.enrollmentsBreakdownTO = enrollmentsBreakdownTO;
	}
	
}
