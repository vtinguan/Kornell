CREATE TABLE IF NOT EXISTS Course (
  uuid char(36) NOT NULL,
  code varchar(255) DEFAULT NULL,
  title varchar(255) DEFAULT NULL,
  description longtext,
  assetsURL varchar(2083) DEFAULT NULL,
  PRIMARY KEY (uuid),
  UNIQUE KEY code (code)
);