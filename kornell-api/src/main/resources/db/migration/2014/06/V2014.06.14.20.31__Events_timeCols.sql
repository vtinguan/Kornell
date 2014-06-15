alter table ActomEntered change column eventFiredAt eventFiredAt char(29);
alter table ActomEntryChangedEvent change column ingestedAt ingestedAt char(29);
alter table AttendanceSheetSigned change column eventFiredAt eventFiredAt char(29);
alter table EnrollmentStateChanged change column eventFiredAt eventFiredAt char(29);
ALTER TABLE ActomEntered ADD PRIMARY KEY (uuid);
