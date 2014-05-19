create table ActomEntryChangedEvent(
	uuid char(36) primary key,
	enrollment_uuid char(36) references Enrollment(uuid), 
	actomKey varchar(1024),
	entryKey varchar(255),
	entryValue varchar(255),
	ingestedAt datetime
);