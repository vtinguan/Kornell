SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='CourseClass' 
          AND column_name='registrationEnrollmentType'
    ) > 0,
    "SELECT 0",
    "ALTER TABLE CourseClass ADD registrationEnrollmentType char(36) not null default '';"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;


SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='CourseClass' 
          AND column_name='institutionRegistrationPrefix'
    ) > 0,
    "SELECT 0",
    "ALTER TABLE CourseClass ADD institutionRegistrationPrefix char(36);"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;