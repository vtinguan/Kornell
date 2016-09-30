CREATE TABLE IF NOT EXISTS CourseDetailsHint (
	uuid char(36) NOT NULL,
	text text NOT NULL,
	entityType char(16) NOT NULL,
	entityUUID char(36) NOT NULL,
	`index` tinyint NOT NULL,
	fontAwesomeClassName char(255),
	PRIMARY KEY (`uuid`)
);

CREATE TABLE IF NOT EXISTS CourseDetailsLibrary (
	uuid char(36) NOT NULL,
	title varchar(255) NOT NULL,
	description text,
	entityType varchar(16) NOT NULL,
	entityUUID varchar(36) NOT NULL,
	`index` tinyint NOT NULL,
	size integer NOT NULL,
	path varchar(512) NOT NULL,
	uploadDate datetime not null,
	fontAwesomeClassName varchar(255),
	PRIMARY KEY (`uuid`)
);

CREATE TABLE IF NOT EXISTS CourseDetailsSection (
	uuid char(36) NOT NULL,
	title varchar(255) NOT NULL,
	text text,
	entityType varchar(16) NOT NULL,
	entityUUID varchar(36) NOT NULL,
	`index` tinyint NOT NULL,
	PRIMARY KEY (`uuid`)
);