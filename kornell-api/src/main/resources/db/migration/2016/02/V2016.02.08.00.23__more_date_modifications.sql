update ActomEntryChangedEvent set ingestedAt = replace(replace(replace(replace(replace(replace(ingestedAt, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), '+00:00', ''), 'Z', '');
update Enrollment set lastProgressUpdate = replace(replace(replace(replace(replace(replace(lastProgressUpdate, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), '+00:00', ''), 'Z', '');
update Enrollment set lastAssessmentUpdate = replace(replace(replace(replace(replace(replace(lastAssessmentUpdate, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), '+00:00', ''), 'Z', '');
update Enrollment set certifiedAt = replace(replace(replace(replace(replace(replace(certifiedAt, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), '+00:00', ''), 'Z', '');
update Enrollment set lastBilledAt = replace(replace(replace(replace(replace(replace(lastBilledAt, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), '+00:00', ''), 'Z', '');

alter table ActomEntryChangedEvent modify ingestedAt timestamp null default null;
alter table Enrollment modify lastProgressUpdate timestamp null default null;
alter table Enrollment modify lastAssessmentUpdate timestamp null default null;
alter table Enrollment modify certifiedAt timestamp null default null;
alter table Enrollment modify lastBilledAt timestamp null default null;