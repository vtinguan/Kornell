alter table CourseClass add column state char(36) not null default 'active';

create table CourseClassStateChanged(
  uuid char(36) not null,
  eventFiredAt char(29) not null,
  personUUID char(36) not null,
  courseClassUUID char(36) not null references CourseClass(uuid),
  fromState char(36) not null,
  toState char(36) not null,
    PRIMARY KEY (uuid)
);