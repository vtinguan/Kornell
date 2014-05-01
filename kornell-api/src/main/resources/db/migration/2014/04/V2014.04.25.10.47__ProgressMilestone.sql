create table ProgressMilestone(
	uuid char(36) not null primary key,
	courseVersionUUID char(36) references CourseVersion(uuid),
	actomKey varchar(1024) not null,
	entryValue varchar(255) not null,
	progress int not null
);