create table CourseVersionInfo(
	uuid char(36) not null primary key,
	courseVersionUUID char(36) not null references CourseVersion(uuid),
	category varchar(255),
	subcategory varchar(255),
	sequence int not null,
	title varchar(255),
	text mediumtext
);