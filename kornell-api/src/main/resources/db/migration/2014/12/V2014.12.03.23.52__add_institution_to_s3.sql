alter table S3ContentRepository add column institutionUUID char(36) not null;

update S3ContentRepository set institutionUUID = '00a4966d-5442-4a44-9490-ef36f133a259' where bucketname = 'midway';
update S3ContentRepository set institutionUUID = '82AEF19C-6A5A-482F-9872-5E7C421C9548' where bucketname = 'prismafs';
update S3ContentRepository set institutionUUID = 'c5c1770b-5f0b-4940-87bd-310ad301b972' where bucketname = 'unicc';
update S3ContentRepository set institutionUUID = 'cw8305fb-0fd5-418a-a17e-9c6b853a6ef2' where bucketname = 'craftware';