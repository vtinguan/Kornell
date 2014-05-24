alter table CourseClass add createdAt datetime default now();
alter table CourseClass add createdBy char(36);
