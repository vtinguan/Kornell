CREATE TABLE Person (
  uuid char(36) NOT NULL PRIMARY KEY,
  fullName varchar(255)
);

CREATE TABLE Password (
  username varchar(63) NOT NULL PRIMARY KEY,
  password varchar(255) DEFAULT NULL,
  person_uuid char(36) DEFAULT NULL,
  FOREIGN KEY (person_uuid) REFERENCES Person(uuid)
);

CREATE TABLE Role (
  username varchar(63) not null,
  role varchar(255) not null,
  foreign key (username) references Password(username),
  primary key (username, role)
);