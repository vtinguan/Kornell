SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Password' 
          AND column_name='uuid'
    ) > 0,
    "SELECT 0",
    "ALTER TABLE Password ADD uuid CHAR(36) NOT NULL;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;

update Password set uuid = uuid();

SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Password' 
          AND column_name='uuid'
    ) < 1,
    "SELECT 0",
    "alter table Password drop primary key, add primary key(uuid);"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;