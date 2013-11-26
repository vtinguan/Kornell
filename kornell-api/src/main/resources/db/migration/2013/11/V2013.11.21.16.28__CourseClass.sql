create table if not exists CourseClass(
	uuid char(36) not null primary key,
	name varchar(255),
	courseVersion_uuid char(36) not null references CourseVersion(uuid),
	institution_uuid char(36) not null references Institution(uuid)
);

alter table Course drop column institution_uuid;
alter table Enrollment drop foreign key Enrollment_Course;
alter table Enrollment drop column course_uuid;
alter table Enrollment add column class_uuid char(36) not null references CourseClass(uuid);

alter table ActomEntered drop column course_uuid;
alter table ActomEntered add column courseClass_uuid char(36) not null references CourseClass(uuid);

--DATA NEEDS FIXING MANUALLY. SORRY :(