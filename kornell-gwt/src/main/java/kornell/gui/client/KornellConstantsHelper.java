package kornell.gui.client;

import kornell.core.error.KornellErrorTO;

import com.google.gwt.core.client.GWT;

public class KornellConstantsHelper {

	private static KornellConstants constants = GWT.create(KornellConstants.class);

	public static String getNotFoundMessage(KornellErrorTO kornellErrorTO) {
		switch (kornellErrorTO.getMessageKey()) {
		case "personNotFound":
			return constants.personNotFound();
		case "repositoryNotFound":
			return constants.repositoryNotFound();
		case "classNotFound":
			return constants.classNotFound();
		case "personOrInstitutionNotFound":
			return constants.personOrInstitutionNotFound();
		default:
			return "Error message not set for key [" + kornellErrorTO.getMessageKey() + "]";
		}
	}

	public static String getInternalServerErrorMessage(KornellErrorTO kornellErrorTO) {
		switch (kornellErrorTO.getMessageKey()) {
		case "errorGeneratingReport":
			return constants.errorGeneratingReport();
		case "errorCheckingCerts":
			return constants.errorCheckingCerts();
		default:
			return "Error message not set for key [" + kornellErrorTO.getMessageKey() + "]";
		}


	}

	public static String getUnauthorizedMessage(KornellErrorTO kornellErrorTO) {
		switch (kornellErrorTO.getMessageKey()) {
		case "authenticationFailed":
			return constants.authenticationFailed();
		case "mustAuthenticate":
			return constants.mustAuthenticate();
		case "passwordChangeFailed":
			return constants.passwordChangeFailed();
		case "passwordChangeDenied":
			return constants.passwordChangeDenied();
		case "classNoRights":
			return constants.classNoRights();
		case "unauthorizedAccessReport":
			return constants.unauthorizedAccessReport();
		case "accessDenied":
			return constants.accessDenied();
		default:
			return "Error message not set for key [" + kornellErrorTO.getMessageKey() + "]";
		}
	}

	public static String getConflictMessage(KornellErrorTO kornellErrorTO) {
		switch (kornellErrorTO.getMessageKey()) {
		case "courseClassAlreadyExists":
			return constants.courseClassAlreadyExists();
		case "courseVersionAlreadyExists":
			return constants.courseVersionAlreadyExists();
		case "invalidValue":
			return constants.invalidValue();
		case "constraintViolatedUUIDName":
			return constants.constraintViolatedUUIDName();
		case "userAlreadyEnrolledInClass":
			return constants.userAlreadyEnrolledInClass();
		default:
			return "Error message not set for key [" + kornellErrorTO.getMessageKey() + "]";
		}
	}
	
	public static String getForbiddenMessage(KornellErrorTO kornellErrorTO) {
		switch (kornellErrorTO.getMessageKey()) {
		default:
			return "Error message not set for key [" + kornellErrorTO.getMessageKey() + "]";
		}
	}
}
