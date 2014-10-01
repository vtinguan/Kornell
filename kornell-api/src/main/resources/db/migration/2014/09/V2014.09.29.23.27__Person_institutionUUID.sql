ALTER TABLE Password ADD uuid CHAR(36) NOT NULL;
update Password set uuid = uuid();

SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Password' 
          AND column_name='uuid'
    ) < 1,
    "SELECT 0",
    "alter table Password drop primary key, add primary key(uuid);"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;


ALTER TABLE Person ADD institutionUUID CHAR(36) NOT NULL;

UPDATE Person p, Registration r
SET p.institutionUUID = r.institution_uuid
WHERE p.uuid = r.person_uuid;

ALTER TABLE `ebdb`.Person 
ADD INDEX `fk_person_institution_institutionUUID_idx` (`institutionUUID` ASC);

ALTER TABLE `ebdb`.Person 
ADD CONSTRAINT `fk_person_institution_institutionUUID`
  FOREIGN KEY (`institutionUUID`)
  REFERENCES `ebdb`.Institution (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

drop index Person_cpf on Person;
drop index Person_email on Person;

create unique index person_cpf_institution on Person(cpf, institutionUUID);
create unique index person_email_institution on Person(email, institutionUUID);




ALTER TABLE Password ADD institutionUUID CHAR(36) NOT NULL;

UPDATE Password p, Person r
SET p.institutionUUID = r.institutionUUID
WHERE p.person_uuid = r.uuid;

ALTER TABLE `ebdb`.Password 
ADD INDEX `fk_password_institution_institutionUUID_idx` (`institutionUUID` ASC);

ALTER TABLE `ebdb`.Password 
ADD CONSTRAINT `fk_password_institution_institutionUUID`
  FOREIGN KEY (`institutionUUID`)
  REFERENCES `ebdb`.Institution (`uuid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

drop index Password_username on Password;
create unique index password_username_institution on Password(username, institutionUUID);



ALTER TABLE Person ADD termsAcceptedOn datetime DEFAULT NULL;

UPDATE Person p, Registration r
SET p.termsAcceptedOn = r.termsAcceptedOn
WHERE p.uuid = r.person_uuid;

drop table Registration;



