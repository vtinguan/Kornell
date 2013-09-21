CREATE TABLE IF NOT EXISTS Role (
  username varchar(63) NOT NULL,
  role varchar(255) NOT NULL,
  PRIMARY KEY (username,role),
  CONSTRAINT Role_ibfk_1 FOREIGN KEY (username) REFERENCES Password (username)
);