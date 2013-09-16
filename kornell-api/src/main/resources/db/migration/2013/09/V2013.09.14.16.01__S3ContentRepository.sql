create table S3ContentRepository(
	uuid char(36) not null primary key,
	accessKeyId varchar(32) not null,
	secretAccessKey varchar(64) not null,
	bucketName varchar(1024) not null,
	prefix varchar(1014)
);