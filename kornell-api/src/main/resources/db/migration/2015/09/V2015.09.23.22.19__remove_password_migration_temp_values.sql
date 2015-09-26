SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Password_bak' 
    ) = 0,
    "SELECT 0",
    "drop table Password_bak;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Password' 
          AND column_name='migrated'
    ) = 0,
    "SELECT 0",
    "alter table Password drop column migrated;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;