alter table ActomEntries modify column entryKey varchar(255);
alter table ActomEntries modify column actomKey varchar(255);
alter table ActomEntered change column actom_key actomKey varchar(255);
create unique index unique_entry on ActomEntries (enrollment_uuid,actomKey,entryKey);