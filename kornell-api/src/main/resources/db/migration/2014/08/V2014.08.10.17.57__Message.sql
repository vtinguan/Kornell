drop table if exists Message;
create table if not exists Message (
  uuid char(36) not null primary key,
  subject varchar(255),
  body longtext,
  senderUUID char(36) not null references Person(uuid),
  parentMessageUUID char(36) references Message(uuid),
  sentAt datetime not null
);

drop table if exists MessagePerson;
create table if not exists MessagePerson (
  uuid char(36) not null primary key,
  messageUUID char(36) references Message(uuid),
  recipientUUID char(36) not null references Person(uuid),
  institutionUUID char(36) not null references Institution(uuid),
  readAt datetime not null,
  archivedAt datetime not null
);