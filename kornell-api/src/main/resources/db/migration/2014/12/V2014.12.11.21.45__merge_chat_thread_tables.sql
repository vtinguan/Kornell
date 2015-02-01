alter table ChatThread add column courseClassUUID char(36);
alter table ChatThread add column personUUID char(36);
alter table ChatThread add column threadType char(36);
alter table ChatThread add column active tinyint(1);

update ChatThread ct inner join CourseClassSupportChatThread cc on ct.uuid = cc.chatThreadUUID
    set ct.courseClassUUID = cc.courseClassUUID,
        ct.personUUID = cc.personUUID,
        ct.threadType = 'SUPPORT',
        ct.active = 1;

alter table ChatThread add constraint fk_chatthread_courseclass_courseclassUUID foreign key (courseClassUUID) references CourseClass (uuid);
alter table ChatThread add constraint fk_chatthread_person_personUUID foreign key (personUUID) references Person (uuid);

drop table CourseClassSupportChatThread;

--add new fields for courseClass-wide thread

alter table ChatThreadParticipant add column active tinyint(1);
update ChatThreadParticipant set active = 1;

alter table ChatThreadParticipant add column lastJoinDate char(29);
update ChatThreadParticipant ctp inner join ChatThread ct on ctp.chatThreadUUID = ct.uuid set ctp.lastJoinDate = ct.createdAt;

alter table CourseClass add column courseClassChatEnabled tinyint(1);
update CourseClass set courseClassChatEnabled = 0;