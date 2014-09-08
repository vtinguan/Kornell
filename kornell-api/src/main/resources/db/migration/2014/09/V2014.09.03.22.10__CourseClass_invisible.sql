SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='CourseClass' 
          AND column_name='invisible'
    ) > 0,
    "SELECT 0",
    "ALTER TABLE CourseClass ADD invisible boolean DEFAULT FALSE;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;