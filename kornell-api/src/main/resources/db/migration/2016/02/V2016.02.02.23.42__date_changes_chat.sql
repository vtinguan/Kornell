update ChatThread set createdAt = replace(replace(replace(replace(replace(createdAt, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), 'Z', '');
update ChatThreadMessage set sentAt = replace(replace(replace(replace(replace(sentAt, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), 'Z', '');
update ChatThreadParticipant set lastReadAt = replace(replace(replace(replace(replace(lastReadAt, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), 'Z', '');
update ChatThreadParticipant set lastJoinDate = replace(replace(replace(replace(replace(lastJoinDate, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), 'Z', '');

alter table ChatThread modify createdAt timestamp;
alter table ChatThreadMessage modify sentAt timestamp;
alter table ChatThreadParticipant modify lastReadAt timestamp;
alter table ChatThreadParticipant modify lastJoinDate timestamp;