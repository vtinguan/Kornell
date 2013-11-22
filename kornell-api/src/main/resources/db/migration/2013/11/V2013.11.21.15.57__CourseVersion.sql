create table CourseVersion(
	uuid char(36) not null primary key,
	name varchar(255),
	repository_uuid char(36) not null,
	course_uuid char(36) not null references Course(uuid)
);

insert into CourseVersion(uuid,name,repository_uuid,course_uuid)
  select UUID(),"v0",repository_uuid,uuid as course_uuid from Course;


alter table Course drop column repository_uuid;