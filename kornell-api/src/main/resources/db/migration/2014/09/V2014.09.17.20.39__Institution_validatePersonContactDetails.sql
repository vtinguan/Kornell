SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Institution' 
          AND column_name='validatePersonContactDetails'
    ) > 0,
    "SELECT 0",
    "ALTER TABLE Institution ADD validatePersonContactDetails boolean DEFAULT FALSE;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
UPDATE Institution set validatePersonContactDetails = 1 WHERE demandsPersonContactDetails = 1;