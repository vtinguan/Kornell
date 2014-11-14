SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Institution' 
          AND column_name='registrationPrefixes'
    ) > 0,
    "SELECT 0",
    "ALTER TABLE Institution ADD registrationPrefixes char(200);"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;

