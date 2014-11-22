create table ContentStore(
	uuid char(36) not null primary key,
	contentStoreType varchar(63) 
);

rename table S3ContentRepository to S3ContentStore;

create table FSContentStore(
	uuid char(36) not null primary key,
	path varchar(2083),
	foreign key (uuid) references ContentStore(uuid)
);