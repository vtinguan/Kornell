drop database if exists ebdb; 
create database if not exists ebdb;
  
grant all on ebdb.* to kornell@'localhost' identified by '42kornell73';
grant all on ebdb.* to kornell@'%' identified by '42kornell73';
  -- ./2013/03/001 - Person and Auth.sql
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
);-- ./2013/03/002 - Course.sql
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
);-- ./2013/06/001 - Institution.sql
create table Institution(
    uuid char(36) not null primary key,
    name varchar(255) not null,
    terms mediumtext
);-- ./2013/06/002 - Registration.sql
create table registration(
    person_uuid char(36) not null,
    institution_uuid char(36) not null,
    termsAcceptedOn datetime,
    primary key(person_uuid,institution_uuid)
);