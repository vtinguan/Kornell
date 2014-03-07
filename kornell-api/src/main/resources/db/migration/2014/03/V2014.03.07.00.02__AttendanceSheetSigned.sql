create table AttendanceSheetSigned(
	uuid char(36) not null,
	institutionUUID char(36) not null,
	personUUID char(36) not null,
	eventFiredAt datetime not null,
  	PRIMARY KEY (uuid)
);