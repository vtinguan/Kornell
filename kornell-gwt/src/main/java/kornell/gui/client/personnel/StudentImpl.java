package kornell.gui.client.personnel;

import kornell.core.entity.EnrollmentProgress;
import kornell.core.entity.EnrollmentProgressDescription;
import static kornell.core.entity.EnrollmentProgressDescription.*;
import kornell.core.entity.EntityFactory;
import kornell.core.to.CourseClassTO;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.entity.Entities;

public class StudentImpl implements Student {


	private CourseClassTO courseClassTO;

	public StudentImpl(CourseClassTO courseClassTO, UserInfoTO userInfoTO) {
		this.courseClassTO = courseClassTO;
	}

	@Override
	public boolean isEnrolled() {
		return courseClassTO.getEnrollment() != null;
	}

	@Override
	public EnrollmentProgress getEnrollmentProgress() {
		Integer progress = courseClassTO.getEnrollment().getProgress();
		if (progress != null) {
			return enrollmentProgressOf(progress);
		} else
			return null;
	}

	private EnrollmentProgress enrollmentProgressOf(Integer progress) {

		EnrollmentProgress ep = Entities.get().newEnrollmentProgress();
		if (progress != null) {
			if (progress <= 0) {
				ep.setProgress(0);
				ep.setDescription(notStarted);
			} else if (progress >= 100) {
				ep.setProgress(100);
				ep.setDescription(completed);
			} else {
				ep.setProgress(progress);
				ep.setDescription(inProgress);
			}
			return ep;
		} else
			return null;

	}

}
