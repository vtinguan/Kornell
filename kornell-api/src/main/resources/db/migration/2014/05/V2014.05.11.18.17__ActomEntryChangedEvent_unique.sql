alter table ActomEntryChangedEvent change column actomKey actomKey varchar(127);
alter table ActomEntered change column actomKey actomKey varchar(127);
alter table ActomEntries change column actomKey actomKey varchar(127);

alter table ActomEntryChangedEvent change column entryKey entryKey varchar(127);
alter table ActomEntries change column entryKey entryKey varchar(127);

alter table ActomEntryChangedEvent change column entryValue entryValue varchar(127);
alter table ActomEntries change column entryValue entryValue varchar(127);

create unique index ActomEntryChangedEvent_PerEntry 
 on ActomEntryChangedEvent (enrollment_uuid, actomKey, entryKey, entryValue);