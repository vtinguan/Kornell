CREATE TABLE IF NOT EXISTS Registration (
  person_uuid char(36) NOT NULL,
  institution_uuid char(36) NOT NULL,
  termsAcceptedOn datetime DEFAULT NULL,
  PRIMARY KEY (person_uuid,institution_uuid)
);