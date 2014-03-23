ALTER TABLE Course ADD UNIQUE (title);
ALTER TABLE CourseVersion ADD UNIQUE (name);
ALTER TABLE CourseClass ADD UNIQUE (name);
ALTER TABLE Institution ADD UNIQUE (name);