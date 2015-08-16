update CourseClass set tutorChatEnabled = 0 where tutorChatEnabled <> 1 or tutorChatEnabled is null;
ALTER TABLE CourseClass MODIFY tutorChatEnabled tinyint(1) NOT NULL DEFAULT 0;

update CourseClass set allowBatchCancellation = 0 where allowBatchCancellation <> 1 or allowBatchCancellation is null;
ALTER TABLE CourseClass MODIFY allowBatchCancellation tinyint(1) NOT NULL DEFAULT 0;

update CourseClass set courseClassChatEnabled = 0 where courseClassChatEnabled <> 1 or courseClassChatEnabled is null;
ALTER TABLE CourseClass MODIFY courseClassChatEnabled tinyint(1) NOT NULL DEFAULT 0;

update CourseClass set publicClass = 0 where publicClass <> 1 or publicClass is null;
ALTER TABLE CourseClass MODIFY publicClass tinyint(1) NOT NULL DEFAULT 0;

update CourseClass set overrideEnrollments = 0 where overrideEnrollments <> 1 or overrideEnrollments is null;
ALTER TABLE CourseClass MODIFY overrideEnrollments tinyint(1) NOT NULL DEFAULT 0;

update CourseClass set invisible = 0 where invisible <> 1 or invisible is null;
ALTER TABLE CourseClass MODIFY invisible tinyint(1) NOT NULL DEFAULT 0;

