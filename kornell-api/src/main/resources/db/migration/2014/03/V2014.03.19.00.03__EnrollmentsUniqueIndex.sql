ALTER TABLE Enrollment ADD UNIQUE idx_person_courseClass(person_uuid, class_uuid);