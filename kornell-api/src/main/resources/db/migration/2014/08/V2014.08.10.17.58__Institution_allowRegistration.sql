SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Institution' 
          AND column_name='allowRegistration'
    ) > 0,
    "SELECT 0",
    "ALTER TABLE Institution ADD allowRegistration tinyint(1) NOT NULL DEFAULT 1;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;