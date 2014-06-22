ALTER TABLE Enrollment CHANGE COLUMN assessmentScore assessmentScore DECIMAL(8,5);
ALTER TABLE Enrollment CHANGE COLUMN lastProgressUpdate lastProgressUpdate char(29);
ALTER TABLE Enrollment CHANGE COLUMN lastAssessmentUpdate lastAssessmentUpdate char(29);
ALTER TABLE Enrollment CHANGE COLUMN certifiedAt certifiedAt char(29);
