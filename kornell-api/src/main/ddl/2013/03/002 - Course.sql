CREATE TABLE Course (
  uuid char(36) NOT NULL PRIMARY KEY,
  code varchar(255),
  title varchar(255),
  description longtext,
  assetsURL varchar(2083),
  UNIQUE KEY code (code)
);

CREATE TABLE Enrollment (
  uuid char(36) NOT NULL PRIMARY KEY,
  enrolledOn datetime,
  course_uuid char(36),
  person_uuid char(36),
  progress decimal(3,2),
  FOREIGN KEY (course_uuid) REFERENCES Course (uuid),
  FOREIGN KEY (person_uuid) REFERENCES Person (uuid)
);