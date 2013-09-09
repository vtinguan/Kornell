CREATE TABLE IF NOT EXISTS Institution (
  uuid char(36) NOT NULL,
  `name` varchar(255) NOT NULL,
  terms mediumtext,
  PRIMARY KEY (uuid)
);