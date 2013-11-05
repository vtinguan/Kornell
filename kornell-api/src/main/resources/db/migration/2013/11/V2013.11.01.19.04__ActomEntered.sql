create table ActomEntered(
	uuid char(36) not null,
	person_uuid char(36) not null,
	actom_key varchar(1024) not null,
	eventFiredAt datetime not null
);