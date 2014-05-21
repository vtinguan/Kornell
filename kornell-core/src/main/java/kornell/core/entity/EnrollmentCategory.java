package kornell.core.entity;

public class EnrollmentCategory {
	
	public static boolean isFinished(Enrollment enrollment) {
		return enrollment.getCertifiedAt() != null;
	}

	public static EnrollmentProgressState getEnrollmentProgressState(Enrollment enrollment) {
		if(isFinished(enrollment)){
			return EnrollmentProgressState.finished;
		} else if(enrollment.getProgress() == null || enrollment.getProgress() == 0){
			return EnrollmentProgressState.toStart;
		} else {
			return EnrollmentProgressState.inProgress;
		}
	}
}