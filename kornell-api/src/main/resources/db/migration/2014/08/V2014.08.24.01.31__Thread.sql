

DROP TABLE IF EXISTS `ebdb`.`Message`;
DROP TABLE IF EXISTS `ebdb`.`MessagePerson`;
DROP TABLE IF EXISTS `ebdb`.`CourseClassSupportChatThread`;
DROP TABLE IF EXISTS `ebdb`.`ChatThreadMessage`;
DROP TABLE IF EXISTS `ebdb`.`ChatThreadParticipant`;
DROP TABLE IF EXISTS `ebdb`.`ChatThread`;

CREATE  TABLE IF NOT EXISTS `ebdb`.`ChatThread` (
  `uuid` CHAR(36) NOT NULL ,
  `createdAt` CHAR(29) NULL ,
  `institutionUUID` CHAR(36) NOT NULL,
  PRIMARY KEY (`uuid`) ,
  INDEX `a_idx` (`institutionUUID` ASC) ,
  CONSTRAINT `fk_thread_institution_institutionUUID`
    FOREIGN KEY (`institutionUUID` )
    REFERENCES `ebdb`.`Institution` (`uuid` )
    ON DELETE RESTRICT
    ON UPDATE NO ACTION);

CREATE  TABLE IF NOT EXISTS `ebdb`.`ChatThreadParticipant` (
  `uuid` CHAR(36) NOT NULL ,
  `chatThreadUUID` CHAR(36) NOT NULL ,
  `personUUID` VARCHAR(45) NOT NULL ,
  `chatThreadName` VARCHAR(255) ,
  `lastReadAt` CHAR(29) NULL ,
  PRIMARY KEY (`uuid`) ,
  INDEX `a_idx` (`chatThreadUUID` ASC) ,
  CONSTRAINT `fk_threadparticipant_chatthread_chatthreadUUID`
    FOREIGN KEY (`chatThreadUUID` )
    REFERENCES `ebdb`.`ChatThread` (`uuid` )
    ON DELETE RESTRICT
    ON UPDATE NO ACTION ,
  INDEX `b_idx` (`personUUID` ASC) ,
  CONSTRAINT `fk_chatthreadparticipant_person_personUUID`
    FOREIGN KEY (`personUUID` )
    REFERENCES `ebdb`.`Person` (`uuid` )
    ON DELETE RESTRICT
    ON UPDATE NO ACTION);

CREATE  TABLE IF NOT EXISTS `ebdb`.`ChatThreadMessage` (
  `uuid` CHAR(36) NOT NULL ,
  `chatThreadUUID` CHAR(36) NOT NULL ,
  `sentAt` CHAR(29) NOT NULL,
  `personUUID` CHAR(36) NOT NULL ,
  `message` LONGTEXT NULL ,
  PRIMARY KEY (`uuid`) ,
  INDEX `a_idx` (`chatThreadUUID` ASC) ,
  CONSTRAINT `fk_chatthreadmessage_chatthread_chatthreadUUID`
    FOREIGN KEY (`chatThreadUUID` )
    REFERENCES `ebdb`.`ChatThread` (`uuid` )
    ON DELETE RESTRICT
    ON UPDATE NO ACTION ,
  INDEX `b_idx` (`personUUID` ASC) ,
  CONSTRAINT `fk_chatthreadmessage_person_personUUID`
    FOREIGN KEY (`personUUID` )
    REFERENCES `ebdb`.`Person` (`uuid` )
    ON DELETE RESTRICT
    ON UPDATE NO ACTION);

CREATE  TABLE IF NOT EXISTS `ebdb`.`CourseClassSupportChatThread` (
  `uuid` CHAR(36) NOT NULL ,
  `chatThreadUUID` CHAR(36) NOT NULL ,
  `courseClassUUID` CHAR(36) NOT NULL ,
  `personUUID` CHAR(36) NOT NULL ,
  PRIMARY KEY (`uuid`) ,
  INDEX `a_idx` (`chatThreadUUID` ASC) ,
  CONSTRAINT `fk_courseclasssupportchatthread_chatthread_chatthreadUUID`
    FOREIGN KEY (`chatThreadUUID` )
    REFERENCES `ebdb`.`ChatThread` (`uuid` )
    ON DELETE RESTRICT
    ON UPDATE NO ACTION ,
  INDEX `b_idx` (`courseClassUUID` ASC) ,
  CONSTRAINT `fk_courseclasssupportchatthread_courseclass_courseclassUUID`
    FOREIGN KEY (`courseClassUUID` )
    REFERENCES `ebdb`.`CourseClass` (`uuid` )
    ON DELETE RESTRICT
    ON UPDATE NO ACTION ,
  INDEX `c_idx` (`personUUID` ASC) ,
  CONSTRAINT `fk_courseclasssupportchatthread_person_personUUID`
    FOREIGN KEY (`personUUID` )
    REFERENCES `ebdb`.`Person` (`uuid` )
    ON DELETE RESTRICT
    ON UPDATE NO ACTION);