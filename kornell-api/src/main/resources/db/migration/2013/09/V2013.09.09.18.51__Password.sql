CREATE TABLE IF NOT EXISTS `Password` (
  username varchar(63) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  person_uuid char(36) DEFAULT NULL,
  PRIMARY KEY (username),
  KEY person_uuid (person_uuid),
  CONSTRAINT Password_ibfk_1 FOREIGN KEY (person_uuid) REFERENCES Person (uuid)
);