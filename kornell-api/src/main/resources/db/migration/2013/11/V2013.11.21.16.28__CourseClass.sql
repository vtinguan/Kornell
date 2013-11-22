create table CourseClass(
	uuid char(36) not null primary key,
	name varchar(255),
	courseVersion_uuid char(36) not null references CourseVersion(uuid),
	institution_uuid char(36) not null references Institution(uuid)
);

insert into CourseClass(uuid,name,courseVersion_uuid,institution_uuid)
  select UUID(),"cc0",CV.uuid,C.institution_uuid as courseVersion_uuid 
  from CourseVersion CV join Course C on CV.course_uuid = C.uuid;
  
alter table Course drop column institution_uuid;
alter table Enrollment drop foreign key Enrollment_Course;
alter table Enrollment drop column course_uuid;
alter table Enrollment add column class_uuid char(36) not null references CourseClass(uuid);

-- UPDATE ENROLLMENTS LIKE A JERK. SORRY BOUT THAT.
update enrollment set class_uuid = (select uuid from CourseClass limit 1);

alter table ActomEntered drop column course_uuid;
alter table ActomEntered add column courseClass_uuid char(36) not null references CourseClass(uuid);
