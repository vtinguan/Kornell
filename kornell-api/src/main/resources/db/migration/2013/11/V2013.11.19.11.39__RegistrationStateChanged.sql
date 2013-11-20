create table EnrollmentStateChanged(
	uuid char(36) not null,
	eventFiredAt datetime not null,
	person_uuid char(36) not null,
	enrollment_uuid char(36) not null default 'FIXME-PLS' references Enrollment(uuid),
	fromState char(36) not null,
	toState char(36) not null,
  	PRIMARY KEY (uuid)
);