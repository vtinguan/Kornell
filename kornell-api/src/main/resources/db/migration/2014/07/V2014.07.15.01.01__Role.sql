alter table Role add column person_uuid char(36) references Person(uuid);
update Role r set person_uuid = (select person_uuid  from Password p where p.username=r.username);
alter table Role drop column username; 