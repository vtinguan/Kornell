ALTER TABLE ActomEntered 
ADD INDEX `fk_actomentered_enrollment_enrollmentUUID_idx` (`enrollmentUUID` ASC);

ALTER TABLE ActomEntryChangedEvent 
ADD INDEX `fk_actomentrychangedevent_enrollment_enrollmentUUID_idx` (`enrollment_uuid` ASC);

ALTER TABLE AttendanceSheetSigned 
ADD INDEX `fk_attendancesheetsigned_institution_institutionUUID_idx` (`institutionUUID` ASC),
ADD INDEX `fk_attendancesheetsigned_person_personUUID_idx` (`personUUID` ASC);

ALTER TABLE CourseClass 
ADD INDEX `fk_courseclass_courseversion_courseversionUUID_idx` (`courseVersion_uuid` ASC),
ADD INDEX `fk_courseclass_institution_institutionUUID_idx` (`institution_uuid` ASC);

ALTER TABLE CourseClassStateChanged 
ADD INDEX `fk_courseclassstatechanged_person_personUUID_idx` (`personUUID` ASC),
ADD INDEX `fk_coursclassstaechanged_courseclass_courseclassUUID_idx` (`courseClassUUID` ASC);

ALTER TABLE CourseVersion 
ADD INDEX `fk_courseversion_course_courseUUID_idx` (`course_uuid` ASC),
ADD INDEX `fk_courseversion_repositoryUUID_idx` (`repository_uuid` ASC);

ALTER TABLE EnrollmentStateChanged 
ADD INDEX `fk_enrollmentstatchanged_person_personUUID_idx` (`person_uuid` ASC),
ADD INDEX `fk_enrollmentstatechanged_enrollment_enrollmentUUID_idx` (`enrollment_uuid` ASC);

ALTER TABLE InstitutionHostName 
ADD INDEX `fk_institutionhostname_institution_institutionUUID_idx` (`institutionUUID` ASC);

ALTER TABLE ProgressMilestone 
ADD INDEX `fk_progressmilestone_courseVersion_courseVersionUUID_idx` (`courseVersionUUID` ASC);

ALTER TABLE Registration 
ADD INDEX `fk_registration_institution_institutionUUID_idx` (`institution_uuid` ASC);

ALTER TABLE Role 
ADD INDEX `fk_role_person_personUUID_idx` (`person_uuid` ASC);

ALTER TABLE ActomEntered 
ADD CONSTRAINT `fk_actomentered_enrollment_enrollmentUUID`
  FOREIGN KEY (`enrollmentUUID`)
  REFERENCES Enrollment (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE ActomEntries 
ADD CONSTRAINT `fk_actomentries_enrollment_enrollmentUUID`
  FOREIGN KEY (`enrollment_uuid`)
  REFERENCES Enrollment (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE AttendanceSheetSigned 
ADD CONSTRAINT `fk_attendancesheetsigned_institution_institutionUUID`
  FOREIGN KEY (`institutionUUID`)
  REFERENCES Institution (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_attendancesheetsigned_person_personUUID`
  FOREIGN KEY (`personUUID`)
  REFERENCES Person (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE CourseClass 
ADD CONSTRAINT `fk_courseclass_courseversion_courseversionUUID`
  FOREIGN KEY (`courseVersion_uuid`)
  REFERENCES CourseVersion (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_courseclass_institution_institutionUUID`
  FOREIGN KEY (`institution_uuid`)
  REFERENCES Institution (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE CourseClassStateChanged 
ADD CONSTRAINT `fk_courseclassstatechanged_person_personUUID`
  FOREIGN KEY (`personUUID`)
  REFERENCES Person (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_coursclassstaechanged_courseclass_courseclassUUID`
  FOREIGN KEY (`courseClassUUID`)
  REFERENCES CourseClass (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE InstitutionHostName 
ADD CONSTRAINT `fk_institutionhostname_institution_institutionUUID`
  FOREIGN KEY (`institutionUUID`)
  REFERENCES Institution (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE ProgressMilestone 
ADD CONSTRAINT `fk_progressmilestone_courseVersion_courseVersionUUID`
  FOREIGN KEY (`courseVersionUUID`)
  REFERENCES CourseVersion (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE Role 
ADD CONSTRAINT `fk_role_person_personUUID`
  FOREIGN KEY (`person_uuid`)
  REFERENCES Person (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;