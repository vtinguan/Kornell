package kornell.gui.client;

import com.google.gwt.i18n.client.ConstantsWithLookup;

public interface KornellConstants extends ConstantsWithLookup {
	
	@DefaultStringValue("skins/first/icons/")
	String imagesPath();
	
	@DefaultStringValue("Loading...")
	String loading();

	/**
	 * VitrinePlace
	 */
	
	@DefaultStringValue("Select a language")
	String selectLanguage();
	
	@DefaultStringValue("Invalid username or password, please try again.")
	String badUsernamePassword();
	
	@DefaultStringValue("Your name must be at least 2 characters long.")
	String nameTooShort();

	@DefaultStringValue("Invalid email address.")
	String invalidEmail();
	
	@DefaultStringValue("Your password must be at least 6 characters long")
	String invalidPasswordTooShort();
	
	@DefaultStringValue("Your password contains invalid characters.")
	String invalidPasswordBadChar();
	
	@DefaultStringValue("Your passwords don't match.")
	String passwordMismatch();
	
	@DefaultStringValue("User created successfully.")
	String userCreated();
	
	@DefaultStringValue("Email address already exists.")
	String emailExists();
	
	@DefaultStringValue("Request completed. Please check your email.")
	String requestPasswordReset();
	
	@DefaultStringValue("The request was not successful. Please check that the email was entered correctly.")
	String requestPasswordResetError();
	
	@DefaultStringValue("Password changed successfully.")
	String passwordChangeComplete();
	
	@DefaultStringValue("We could not update your password. Please check your email or make another request.")
	String passwordChangeError();
	
	
	
	
	
	
	
	
	/**
	 * 
	 * GenericWelcomeView
	 * 
	 */
	@DefaultStringValue("All")
	String allClasses();

	@DefaultStringValue("Finished")
	String finished();

	@DefaultStringValue("To start")
	String toStart();

	@DefaultStringValue("In progress")
	String inProgress();

	

	/**
	 * 
	 * GenericCourseDetailsView
	 * 
	 */
	@DefaultStringValue("Course details: ")
	String detailsHeader();
	
	@DefaultStringValue("Class: ")
	String detailsSubHeader();
	
	@DefaultStringValue("About the course")
	String btnAbout();
	
	@DefaultStringValue("Topics")
	String btnTopics();
	
	@DefaultStringValue("Certification")
	String btnCertification();
	
	@DefaultStringValue("Library")
	String btnLibrary();
	
	@DefaultStringValue("General view")
	String btnAboutInfo();
	
	@DefaultStringValue("Main topics covered on this course")
	String btnTopicsInfo();
	
	@DefaultStringValue("Evaluations and tests")
	String btnCertificationInfo();
	
	@DefaultStringValue("Close Details")
	String closeDetails();

	@DefaultStringValue("Topic")
	String topic();

	
	/**
	 * 
	 * GenericBarView
	 */
	
	@DefaultStringValue("course")
	String course();
	
	@DefaultStringValue("details")
	String details();
	
	@DefaultStringValue("library")
	String library();
	
	@DefaultStringValue("forum")
	String forum();
	
	@DefaultStringValue("chat")
	String chat();
	
	@DefaultStringValue("specialists")
	String specialists();
	
	@DefaultStringValue("notes")
	String notes();
	
	@DefaultStringValue("back")
	String back();
	
	@DefaultStringValue("next")
	String next();
	
	@DefaultStringValue("previous")
	String previous();
	
	@DefaultStringValue("institution")
	String institution();
	
	@DefaultStringValue("courses")
	String courses();
	
	@DefaultStringValue("versions")
	String versions();
	
	@DefaultStringValue("classes")
	String classes();
	
	/**
	 * 
	 * Util
	 */
	@DefaultStringValue("January")
	String january();
	@DefaultStringValue("February")
	String february();
	@DefaultStringValue("March")
	String march();
	@DefaultStringValue("April")
	String april();
	@DefaultStringValue("May")
	String may();
	@DefaultStringValue("June")
	String june();
	@DefaultStringValue("July")
	String july();
	@DefaultStringValue("August")
	String august();
	@DefaultStringValue("September")
	String september();
	@DefaultStringValue("October")
	String october();
	@DefaultStringValue("November")
	String november();
	@DefaultStringValue("December")
	String december();
	


	

	/**
	 * 
	 * EnrollmentState
	 */
	@DefaultStringValue("notEnrolled")
	String notEnrolled();
	@DefaultStringValue("enrolled")
	String enrolled();
	@DefaultStringValue("requested")
	String requested();
	@DefaultStringValue("denied")
	String denied();
	@DefaultStringValue("cancelled")
	String cancelled();
	


	

	/**
	 * 
	 * Message
	 */
	@DefaultStringValue("Help")
	String composeTitle();
	@DefaultStringValue("Leave your questions or suggestions here.")
	String composeSubTitle();
	@DefaultStringValue("Class")
	String courseClassAdmin();
	@DefaultStringValue("Institution")
	String institutionAdmin();
	@DefaultStringValue("Recipient:")
	String recipient();
	@DefaultStringValue("Message:")
	String message();
	
	
	/**
	 * Errors 404
	 */
	@DefaultStringValue("Person not found.")
	String personNotFound();
	@DefaultStringValue("Repository not found.")
	String repositoryNotFound();
	@DefaultStringValue("Class not found.")
	String classNotFound();
	@DefaultStringValue("Person or institution not found.")
	String personOrInstitutionNotFound();

	
	/**
	 * Errors 401
	 */
	@DefaultStringValue("Authentication failed.")
	String authenticationFailed();
	@DefaultStringValue("You must authenticate to access this path.")
	String mustAuthenticate();
	@DefaultStringValue("It wasn't possible to change your password.")
	String passwordChangeFailed();
	@DefaultStringValue("Unauthorized attempt to change the password.")
	String passwordChangeDenied();
	@DefaultStringValue("Unauthorized attempt to update a class without platformAdmin or institutionAdmin rights.")
	String classNoRights();
	@DefaultStringValue("Unauthorized attempt to generate the class' certificates without admin rights.")
	String unauthorizedAccessReport();
	@DefaultStringValue("Access denied.")
	String accessDenied();
	
	
	/**
	 * Errors 409
	 */
	
	@DefaultStringValue("A class with this name already exists.")
	String courseClassAlreadyExists();
	@DefaultStringValue("A course version with this name already exists.")
	String courseVersionAlreadyExists();
	@DefaultStringValue("Invalid input value")
	String invalidValue();
	@DefaultStringValue("Constraint Violated (uuid or name).")
	String constraintViolatedUUIDName();
	
	
	/**
	 * Errors 500
	 */
	
	@DefaultStringValue("Error generating the report.")
	String errorGeneratingReport();
	
	@DefaultStringValue("Error checking for certificates.")
	String errorCheckingCerts();
	
}