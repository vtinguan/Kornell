INSERT INTO Curriculum  (
	SELECT i.uuid, c.uuid, now() FROM Course c
	JOIN CourseVersion cv ON cv.course_uuid = c.uuid
	JOIN CourseClass cc ON cc.courseVersion_uuid = cv.uuid
	JOIN Institution i ON cc.institution_uuid = i.uuid
);