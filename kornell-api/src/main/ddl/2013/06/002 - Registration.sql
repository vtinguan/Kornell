create table registration(
    person_uuid char(36) not null,
    institution_uuid char(36) not null,
    termsAcceptedOn datetime,
    primary key(person_uuid,institution_uuid)
);