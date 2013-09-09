CREATE TABLE IF NOT EXISTS Enrollment (
  uuid char(36) NOT NULL,
  enrolledOn datetime DEFAULT NULL,
  course_uuid char(36) DEFAULT NULL,
  person_uuid char(36) DEFAULT NULL,
  progress decimal(3,2) DEFAULT NULL,
  PRIMARY KEY (uuid),
  KEY course_uuid (course_uuid),
  KEY person_uuid (person_uuid),
  CONSTRAINT Enrollment_Course FOREIGN KEY (course_uuid) REFERENCES Course (uuid),
  CONSTRAINT Enrollment_Person FOREIGN KEY (person_uuid) REFERENCES Person (uuid)
);