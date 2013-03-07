CREATE TABLE Person (
  uuid char(36) NOT NULL PRIMARY KEY,
  fullName varchar(255)
);

CREATE TABLE Principal (
  uuid char(36) NOT NULL PRIMARY KEY,
  username varchar(255),
  person_uuid char(36),
  UNIQUE KEY username (username),
  FOREIGN KEY (person_uuid) REFERENCES Person(uuid)
);

CREATE TABLE PasswordCredential (
  uuid char(36) NOT NULL PRIMARY KEY,
  password varchar(255) DEFAULT NULL,
  principal_uuid char(36) DEFAULT NULL,
  FOREIGN KEY (principal_uuid) REFERENCES Principal (uuid)
);

CREATE TABLE Principal_roles (
  principal_uuid  char(36) NOT NULL PRIMARY KEY,
  roles varchar(255) DEFAULT NULL,
  FOREIGN KEY (principal_uuid) REFERENCES Principal(uuid)
);