update ActomEntryChangedEvent set ingestedAt = replace(replace(replace(replace(replace(ingestedAt, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), 'Z', '');
update Enrollment set lastProgressUpdate = replace(replace(replace(replace(replace(lastProgressUpdate, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), 'Z', '');
update Enrollment set lastAssessmentUpdate = replace(replace(replace(replace(replace(lastAssessmentUpdate, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), 'Z', '');
update Enrollment set certifiedAt = replace(replace(replace(replace(replace(certifiedAt, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), 'Z', '');
update Enrollment set lastBilledAt = replace(replace(replace(replace(replace(lastBilledAt, '-02:00', ''), '-03:00', ''), '-04:00', ''), '-05:00', ''), 'Z', '');

alter table ActomEntryChangedEvent modify ingestedAt timestamp;
alter table Enrollment modify lastProgressUpdate timestamp;
alter table Enrollment modify lastAssessmentUpdate timestamp;
alter table Enrollment modify certifiedAt timestamp;
alter table Enrollment modify lastBilledAt timestamp;