DROP TABLE IF EXISTS `ebdb`.`InstitutionRegistrationPrefix`;

CREATE  TABLE IF NOT EXISTS `ebdb`.`InstitutionRegistrationPrefix` (
  `prefix` CHAR(29) NOT NULL ,
  `institutionUUID` CHAR(36) NOT NULL,
  PRIMARY KEY (`institutionUUID`, `prefix`) ,
  INDEX `a_idx` (`institutionUUID` ASC));