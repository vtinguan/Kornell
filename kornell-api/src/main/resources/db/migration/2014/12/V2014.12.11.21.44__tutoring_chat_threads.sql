alter table CourseClassSupportChatThread add supportType char(32) not null;
update CourseClassSupportChatThread set supportType = 'SUPPORT' where supportType is null limit 100;