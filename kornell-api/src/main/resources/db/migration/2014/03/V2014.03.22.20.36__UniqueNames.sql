ALTER TABLE CourseVersion ADD UNIQUE idx_name_course(name, course_uuid);
ALTER TABLE CourseClass ADD UNIQUE idx_name_courseVersion(name, courseVersion_uuid);