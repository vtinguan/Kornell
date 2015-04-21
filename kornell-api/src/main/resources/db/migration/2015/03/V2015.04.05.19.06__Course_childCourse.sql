SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Course' 
          AND column_name='childCourse'
    ) > 0,
    "SELECT 0",
    "alter table Course add column childCourse boolean default false;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;