alter table ActomEntered 
 add column course_uuid char(36) not null default 'FIXME-PLS' references Course(uuid);
