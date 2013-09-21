CREATE TABLE IF NOT EXISTS Person (
  uuid char(36) NOT NULL,
  fullName varchar(255) DEFAULT NULL,
  lastPlaceVisited varchar(2083) DEFAULT NULL,
  PRIMARY KEY (uuid)
);