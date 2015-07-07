alter table Enrollment 
	add column parentEnrollmentUUID char(36);
alter table Enrollment 
	add constraint fk_parent_enrollment 
    foreign key (parentEnrollmentUUID) 
    references Enrollment(uuid);