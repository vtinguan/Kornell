SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

ALTER TABLE `ebdb`.`actomentered` 
ADD INDEX `fk_actomentered_enrollment_enrollmentUUID_idx` (`enrollmentUUID` ASC);

ALTER TABLE `ebdb`.`actomentrychangedevent` 
ADD INDEX `fk_actomentrychangedevent_enrollment_enrollmentUUID_idx` (`enrollment_uuid` ASC);

ALTER TABLE `ebdb`.`attendancesheetsigned` 
ADD INDEX `fk_attendancesheetsigned_institution_institutionUUID_idx` (`institutionUUID` ASC),
ADD INDEX `fk_attendancesheetsigned_person_personUUID_idx` (`personUUID` ASC);

ALTER TABLE `ebdb`.`courseclass` 
ADD INDEX `fk_courseclass_courseversion_courseversionUUID_idx` (`courseVersion_uuid` ASC),
ADD INDEX `fk_courseclass_institution_institutionUUID_idx` (`institution_uuid` ASC);

ALTER TABLE `ebdb`.`courseclassstatechanged` 
ADD INDEX `fk_courseclassstatechanged_person_personUUID_idx` (`personUUID` ASC),
ADD INDEX `fk_coursclassstaechanged_courseclass_courseclassUUID_idx` (`courseClassUUID` ASC);

ALTER TABLE `ebdb`.`courseversion` 
ADD INDEX `fk_courseversion_course_courseUUID_idx` (`course_uuid` ASC),
ADD INDEX `fk_courseversion_repositoryUUID_idx` (`repository_uuid` ASC);

ALTER TABLE `ebdb`.`enrollmentstatechanged` 
ADD INDEX `fk_enrollmentstatchanged_person_personUUID_idx` (`person_uuid` ASC),
ADD INDEX `fk_enrollmentstatechanged_enrollment_enrollmentUUID_idx` (`enrollment_uuid` ASC);

ALTER TABLE `ebdb`.`institutionhostname` 
ADD INDEX `fk_institutionhostname_institution_institutionUUID_idx` (`institutionUUID` ASC);

ALTER TABLE `ebdb`.`messageperson` 
ADD INDEX `fk_messageperson_message_messageUUID_idx` (`messageUUID` ASC),
ADD INDEX `fk_messageperson_institution_institutionUUID_idx` (`institutionUUID` ASC);

ALTER TABLE `ebdb`.`progressmilestone` 
ADD INDEX `fk_progressmilestone_courseVersion_courseVersionUUID_idx` (`courseVersionUUID` ASC);

ALTER TABLE `ebdb`.`registration` 
ADD INDEX `fk_registration_institution_institutionUUID_idx` (`institution_uuid` ASC);

ALTER TABLE `ebdb`.`role` 
ADD INDEX `fk_role_person_personUUID_idx` (`person_uuid` ASC);

ALTER TABLE `ebdb`.`actomentered` 
ADD CONSTRAINT `fk_actomentered_enrollment_enrollmentUUID`
  FOREIGN KEY (`enrollmentUUID`)
  REFERENCES `ebdb`.`enrollment` (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `ebdb`.`actomentries` 
ADD CONSTRAINT `fk_actomentries_enrollment_enrollmentUUID`
  FOREIGN KEY (`enrollment_uuid`)
  REFERENCES `ebdb`.`enrollment` (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `ebdb`.`actomentrychangedevent` 
ADD CONSTRAINT `fk_actomentrychangedevent_enrollment_enrollmentUUID`
  FOREIGN KEY (`enrollment_uuid`)
  REFERENCES `ebdb`.`enrollment` (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `ebdb`.`attendancesheetsigned` 
ADD CONSTRAINT `fk_attendancesheetsigned_institution_institutionUUID`
  FOREIGN KEY (`institutionUUID`)
  REFERENCES `ebdb`.`institution` (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_attendancesheetsigned_person_personUUID`
  FOREIGN KEY (`personUUID`)
  REFERENCES `ebdb`.`person` (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `ebdb`.`courseclass` 
ADD CONSTRAINT `fk_courseclass_courseversion_courseversionUUID`
  FOREIGN KEY (`courseVersion_uuid`)
  REFERENCES `ebdb`.`courseversion` (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_courseclass_institution_institutionUUID`
  FOREIGN KEY (`institution_uuid`)
  REFERENCES `ebdb`.`institution` (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `ebdb`.`courseclassstatechanged` 
ADD CONSTRAINT `fk_courseclassstatechanged_person_personUUID`
  FOREIGN KEY (`personUUID`)
  REFERENCES `ebdb`.`person` (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_coursclassstaechanged_courseclass_courseclassUUID`
  FOREIGN KEY (`courseClassUUID`)
  REFERENCES `ebdb`.`courseclass` (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `ebdb`.`courseversion` 
ADD CONSTRAINT `fk_courseversion_course_courseUUID`
  FOREIGN KEY (`course_uuid`)
  REFERENCES `ebdb`.`course` (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_courseversion_repositoryUUID`
  FOREIGN KEY (`repository_uuid`)
  REFERENCES `ebdb`.`s3contentrepository` (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `ebdb`.`enrollmentstatechanged` 
ADD CONSTRAINT `fk_enrollmentstatchanged_person_personUUID`
  FOREIGN KEY (`person_uuid`)
  REFERENCES `ebdb`.`person` (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_enrollmentstatechanged_enrollment_enrollmentUUID`
  FOREIGN KEY (`enrollment_uuid`)
  REFERENCES `ebdb`.`enrollment` (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `ebdb`.`institutionhostname` 
ADD CONSTRAINT `fk_institutionhostname_institution_institutionUUID`
  FOREIGN KEY (`institutionUUID`)
  REFERENCES `ebdb`.`institution` (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `ebdb`.`messageperson` 
ADD CONSTRAINT `fk_messageperson_message_messageUUID`
  FOREIGN KEY (`messageUUID`)
  REFERENCES `ebdb`.`message` (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_messageperson_institution_institutionUUID`
  FOREIGN KEY (`institutionUUID`)
  REFERENCES `ebdb`.`institution` (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `ebdb`.`progressmilestone` 
ADD CONSTRAINT `fk_progressmilestone_courseVersion_courseVersionUUID`
  FOREIGN KEY (`courseVersionUUID`)
  REFERENCES `ebdb`.`courseversion` (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `ebdb`.`registration` 
ADD CONSTRAINT `fk_registration_person_personUUID`
  FOREIGN KEY (`person_uuid`)
  REFERENCES `ebdb`.`person` (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_registration_institution_institutionUUID`
  FOREIGN KEY (`institution_uuid`)
  REFERENCES `ebdb`.`institution` (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `ebdb`.`role` 
ADD CONSTRAINT `fk_role_person_personUUID`
  FOREIGN KEY (`person_uuid`)
  REFERENCES `ebdb`.`person` (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
