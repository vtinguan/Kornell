SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Enrollment' 
          AND column_name='courseVersionUUID'
    ) > 0,
    "SELECT 0",
    "alter table Enrollment add column courseVersionUUID char(36);"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
ALTER TABLE Enrollment MODIFY class_uuid char(36) DEFAULT NULL;