SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE
          CONSTRAINT_SCHEMA=DATABASE() 
          AND CONSTRAINT_NAME='password_person_institution'
    ) > 0,
    "SELECT 0",
    "alter table Password add constraint password_person_institution unique (person_uuid, institutionUUID);"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;