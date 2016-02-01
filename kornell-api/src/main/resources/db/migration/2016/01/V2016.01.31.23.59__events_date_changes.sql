-- cleanup script
update ActomEntered set eventFiredAt = replace(replace(replace(replace(replace(eventFiredAt, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), 'Z', '');
update AttendanceSheetSigned set eventFiredAt = replace(replace(replace(replace(replace(eventFiredAt, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), 'Z', '');
update EnrollmentStateChanged set eventFiredAt = replace(replace(replace(replace(replace(eventFiredAt, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), 'Z', '');
update EnrollmentTransferred set eventFiredAt = replace(replace(replace(replace(replace(eventFiredAt, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), 'Z', '');
update EntityChanged set eventFiredAt = replace(replace(replace(replace(replace(eventFiredAt, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), 'Z', '');
update CourseClassStateChanged set eventFiredAt = replace(replace(replace(replace(replace(eventFiredAt, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), 'Z', '');

alter table ActomEntered modify eventFiredAt timestamp;
alter table AttendanceSheetSigned modify eventFiredAt timestamp;
alter table EnrollmentStateChanged modify eventFiredAt timestamp;
alter table EnrollmentTransferred modify eventFiredAt timestamp;
alter table EntityChanged modify eventFiredAt timestamp;
alter table CourseClassStateChanged modify eventFiredAt timestamp;