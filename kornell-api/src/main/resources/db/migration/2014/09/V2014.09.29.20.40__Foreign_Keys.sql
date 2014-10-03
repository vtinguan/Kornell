ALTER TABLE `ebdb`.ActomEntered 
ADD INDEX `fk_actomentered_enrollment_enrollmentUUID_idx` (`enrollmentUUID` ASC);

ALTER TABLE `ebdb`.ActomEntryChangedEvent 
ADD INDEX `fk_actomentrychangedevent_enrollment_enrollmentUUID_idx` (`enrollment_uuid` ASC);

ALTER TABLE `ebdb`.AttendanceSheetSigned 
ADD INDEX `fk_attendancesheetsigned_institution_institutionUUID_idx` (`institutionUUID` ASC),
ADD INDEX `fk_attendancesheetsigned_person_personUUID_idx` (`personUUID` ASC);

ALTER TABLE `ebdb`.CourseClass 
ADD INDEX `fk_courseclass_courseversion_courseversionUUID_idx` (`courseVersion_uuid` ASC),
ADD INDEX `fk_courseclass_institution_institutionUUID_idx` (`institution_uuid` ASC);

ALTER TABLE `ebdb`.CourseClassStateChanged 
ADD INDEX `fk_courseclassstatechanged_person_personUUID_idx` (`personUUID` ASC),
ADD INDEX `fk_coursclassstaechanged_courseclass_courseclassUUID_idx` (`courseClassUUID` ASC);

ALTER TABLE `ebdb`.CourseVersion 
ADD INDEX `fk_courseversion_course_courseUUID_idx` (`course_uuid` ASC),
ADD INDEX `fk_courseversion_repositoryUUID_idx` (`repository_uuid` ASC);

ALTER TABLE `ebdb`.EnrollmentStateChanged 
ADD INDEX `fk_enrollmentstatchanged_person_personUUID_idx` (`person_uuid` ASC),
ADD INDEX `fk_enrollmentstatechanged_enrollment_enrollmentUUID_idx` (`enrollment_uuid` ASC);

ALTER TABLE `ebdb`.InstitutionHostName 
ADD INDEX `fk_institutionhostname_institution_institutionUUID_idx` (`institutionUUID` ASC);

ALTER TABLE `ebdb`.ProgressMilestone 
ADD INDEX `fk_progressmilestone_courseVersion_courseVersionUUID_idx` (`courseVersionUUID` ASC);

ALTER TABLE `ebdb`.Registration 
ADD INDEX `fk_registration_institution_institutionUUID_idx` (`institution_uuid` ASC);

ALTER TABLE `ebdb`.Role 
ADD INDEX `fk_role_person_personUUID_idx` (`person_uuid` ASC);

ALTER TABLE `ebdb`.ActomEntered 
ADD CONSTRAINT `fk_actomentered_enrollment_enrollmentUUID`
  FOREIGN KEY (`enrollmentUUID`)
  REFERENCES `ebdb`.Enrollment (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `ebdb`.ActomEntries 
ADD CONSTRAINT `fk_actomentries_enrollment_enrollmentUUID`
  FOREIGN KEY (`enrollment_uuid`)
  REFERENCES `ebdb`.Enrollment (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `ebdb`.AttendanceSheetSigned 
ADD CONSTRAINT `fk_attendancesheetsigned_institution_institutionUUID`
  FOREIGN KEY (`institutionUUID`)
  REFERENCES `ebdb`.Institution (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_attendancesheetsigned_person_personUUID`
  FOREIGN KEY (`personUUID`)
  REFERENCES `ebdb`.Person (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `ebdb`.CourseClass 
ADD CONSTRAINT `fk_courseclass_courseversion_courseversionUUID`
  FOREIGN KEY (`courseVersion_uuid`)
  REFERENCES `ebdb`.CourseVersion (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_courseclass_institution_institutionUUID`
  FOREIGN KEY (`institution_uuid`)
  REFERENCES `ebdb`.Institution (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `ebdb`.CourseClassStateChanged 
ADD CONSTRAINT `fk_courseclassstatechanged_person_personUUID`
  FOREIGN KEY (`personUUID`)
  REFERENCES `ebdb`.Person (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_coursclassstaechanged_courseclass_courseclassUUID`
  FOREIGN KEY (`courseClassUUID`)
  REFERENCES `ebdb`.CourseClass (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `ebdb`.InstitutionHostName 
ADD CONSTRAINT `fk_institutionhostname_institution_institutionUUID`
  FOREIGN KEY (`institutionUUID`)
  REFERENCES `ebdb`.Institution (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `ebdb`.ProgressMilestone 
ADD CONSTRAINT `fk_progressmilestone_courseVersion_courseVersionUUID`
  FOREIGN KEY (`courseVersionUUID`)
  REFERENCES `ebdb`.CourseVersion (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `ebdb`.Role 
ADD CONSTRAINT `fk_role_person_personUUID`
  FOREIGN KEY (`person_uuid`)
  REFERENCES `ebdb`.Person (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;