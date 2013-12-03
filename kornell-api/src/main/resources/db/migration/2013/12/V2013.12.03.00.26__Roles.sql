DROP TABLE IF EXISTS Role;

CREATE TABLE IF NOT EXISTS Role (
  uuid char(36),
  username varchar(63) NOT NULL,
  role varchar(255) NOT NULL,
  institution_uuid char(36),
  PRIMARY KEY (uuid),
  CONSTRAINT Role_ibfk_1 FOREIGN KEY (username) REFERENCES Password (username),
  CONSTRAINT Role_ibfk_2 FOREIGN KEY (institution_uuid) REFERENCES Institution (uuid)
);