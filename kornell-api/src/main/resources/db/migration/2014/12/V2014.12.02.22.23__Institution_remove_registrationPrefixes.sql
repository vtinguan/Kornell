SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Institution' 
          AND column_name='registrationPrefixes'
    ) > 0,
    "ALTER TABLE Institution DROP COLUMN registrationPrefixes;",
    "SELECT 0"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;