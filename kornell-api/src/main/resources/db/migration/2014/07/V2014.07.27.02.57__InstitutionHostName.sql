create table if not exists InstitutionHostName (
  uuid char(36) not null primary key,
  hostName varchar(255),
  institutionUUID char(36) not null references Institution(uuid)
);