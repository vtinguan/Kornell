-- cleanup script
update ActomEntered set eventFiredAt = replace(replace(replace(replace(replace(eventFiredAt, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), 'Z', '');
update AttendanceSheetSigned set eventFiredAt = replace(replace(replace(replace(replace(eventFiredAt, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), 'Z', '');
update EnrollmentStateChanged set eventFiredAt = replace(replace(replace(replace(replace(eventFiredAt, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), 'Z', '');
update EnrollmentTransferred set eventFiredAt = replace(replace(replace(replace(replace(eventFiredAt, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), 'Z', '');
update EntityChanged set eventFiredAt = replace(replace(replace(replace(replace(eventFiredAt, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), 'Z', '');
update CourseClassStateChanged set eventFiredAt = replace(replace(replace(replace(replace(eventFiredAt, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), 'Z', '');

alter table ActomEntered modify eventFiredAt timestamp null default null;
alter table AttendanceSheetSigned modify eventFiredAt timestamp null default null;
alter table EnrollmentStateChanged modify eventFiredAt timestamp null default null;
alter table EnrollmentTransferred modify eventFiredAt timestamp null default null;
alter table EntityChanged modify eventFiredAt timestamp null default null;
alter table CourseClassStateChanged modify eventFiredAt timestamp null default null;