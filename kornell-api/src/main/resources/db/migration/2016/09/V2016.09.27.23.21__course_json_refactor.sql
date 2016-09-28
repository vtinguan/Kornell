CREATE TABLE IF NOT EXISTS CourseDetailsHint (
	uuid char(36) NOT NULL,
	text varchar(1024) NOT NULL,
	entityType varchar(16) NOT NULL,
	entityUUID varchar(36) NOT NULL,
	index tinyint NOT NULL,
	fontAwesomeClassName varchar(255),
	PRIMARY KEY (`uuid`)
);

CREATE TABLE IF NOT EXISTS CourseDetailsLibrary (
	uuid char(36) NOT NULL,
	title varchar(255) NOT NULL,
	description varchar(1024),
	entityType varchar(16) NOT NULL,
	entityUUID varchar(36) NOT NULL,
	index tinyint NOT NULL,
	size integer NOT NULL,
	path varchar(512) NOT NULL,
	uploadDate datetime not null,
	fontAwesomeClassName varchar(255),
	PRIMARY KEY (`uuid`)
);

CREATE TABLE IF NOT EXISTS CourseDetailsSection (
	uuid char(36) NOT NULL,
	title varchar(255) NOT NULL,
	text varchar(1024),
	entityType varchar(16) NOT NULL,
	entityUUID varchar(36) NOT NULL,
	index tinyint NOT NULL,
	PRIMARY KEY (`uuid`)
);