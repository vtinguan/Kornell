alter table CourseClass add column tutorChatEnabled tinyint(1);
update CourseClass set tutorChatEnabled = 0;