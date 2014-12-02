alter table CourseClassSupportChatThread add supportType char(32);
update CourseClassSupportChatThread set supportType = 'SUPPORT' where supportType is null limit 100;
alter table CourseClassSupportChatThread modify supportType char(32) not null;