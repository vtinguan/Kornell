alter table ActomEntered drop column course_uuid if exists;
alter table ActomEntered drop column person_uuid;
alter table ActomEntered add column enrollmentUUID char(36) references Enrollment(uuid);