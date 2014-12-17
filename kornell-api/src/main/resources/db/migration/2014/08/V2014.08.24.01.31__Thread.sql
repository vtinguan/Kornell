

DROP TABLE IF EXISTS `Message`;
DROP TABLE IF EXISTS `MessagePerson`;
DROP TABLE IF EXISTS `CourseClassSupportChatThread`;
DROP TABLE IF EXISTS `ChatThreadMessage`;
DROP TABLE IF EXISTS `ChatThreadParticipant`;
DROP TABLE IF EXISTS `ChatThread`;

CREATE  TABLE IF NOT EXISTS `ChatThread` (
  `uuid` CHAR(36) NOT NULL ,
  `createdAt` CHAR(29) NULL ,
  `institutionUUID` CHAR(36) NOT NULL,
  PRIMARY KEY (`uuid`) ,
  INDEX `a_idx` (`institutionUUID` ASC) ,
  CONSTRAINT `fk_thread_institution_institutionUUID`
    FOREIGN KEY (`institutionUUID` )
    REFERENCES `Institution` (`uuid` )
    ON DELETE RESTRICT
    ON UPDATE NO ACTION);

CREATE  TABLE IF NOT EXISTS `ChatThreadParticipant` (
  `uuid` CHAR(36) NOT NULL ,
  `chatThreadUUID` CHAR(36) NOT NULL ,
  `personUUID` VARCHAR(45) NOT NULL ,
  `chatThreadName` VARCHAR(255) ,
  `lastReadAt` CHAR(29) NULL ,
  PRIMARY KEY (`uuid`) ,
  INDEX `a_idx` (`chatThreadUUID` ASC) ,
  CONSTRAINT `fk_threadparticipant_chatthread_chatthreadUUID`
    FOREIGN KEY (`chatThreadUUID` )
    REFERENCES `ChatThread` (`uuid` )
    ON DELETE RESTRICT
    ON UPDATE NO ACTION ,
  INDEX `b_idx` (`personUUID` ASC) ,
  CONSTRAINT `fk_chatthreadparticipant_person_personUUID`
    FOREIGN KEY (`personUUID` )
    REFERENCES `Person` (`uuid` )
    ON DELETE RESTRICT
    ON UPDATE NO ACTION);

CREATE  TABLE IF NOT EXISTS `ChatThreadMessage` (
  `uuid` CHAR(36) NOT NULL ,
  `chatThreadUUID` CHAR(36) NOT NULL ,
  `sentAt` CHAR(29) NOT NULL,
  `personUUID` CHAR(36) NOT NULL ,
  `message` LONGTEXT NULL ,
  PRIMARY KEY (`uuid`) ,
  INDEX `a_idx` (`chatThreadUUID` ASC) ,
  CONSTRAINT `fk_chatthreadmessage_chatthread_chatthreadUUID`
    FOREIGN KEY (`chatThreadUUID` )
    REFERENCES `ChatThread` (`uuid` )
    ON DELETE RESTRICT
    ON UPDATE NO ACTION ,
  INDEX `b_idx` (`personUUID` ASC) ,
  CONSTRAINT `fk_chatthreadmessage_person_personUUID`
    FOREIGN KEY (`personUUID` )
    REFERENCES `Person` (`uuid` )
    ON DELETE RESTRICT
    ON UPDATE NO ACTION);

CREATE  TABLE IF NOT EXISTS `CourseClassSupportChatThread` (
  `uuid` CHAR(36) NOT NULL ,
  `chatThreadUUID` CHAR(36) NOT NULL ,
  `courseClassUUID` CHAR(36) NOT NULL ,
  `personUUID` CHAR(36) NOT NULL ,
  PRIMARY KEY (`uuid`) ,
  INDEX `a_idx` (`chatThreadUUID` ASC) ,
  CONSTRAINT `fk_courseclasssupportchatthread_chatthread_chatthreadUUID`
    FOREIGN KEY (`chatThreadUUID` )
    REFERENCES `ChatThread` (`uuid` )
    ON DELETE RESTRICT
    ON UPDATE NO ACTION ,
  INDEX `b_idx` (`courseClassUUID` ASC) ,
  CONSTRAINT `fk_courseclasssupportchatthread_courseclass_courseclassUUID`
    FOREIGN KEY (`courseClassUUID` )
    REFERENCES `CourseClass` (`uuid` )
    ON DELETE RESTRICT
    ON UPDATE NO ACTION ,
  INDEX `c_idx` (`personUUID` ASC) ,
  CONSTRAINT `fk_courseclasssupportchatthread_person_personUUID`
    FOREIGN KEY (`personUUID` )
    REFERENCES `Person` (`uuid` )
    ON DELETE RESTRICT
    ON UPDATE NO ACTION);