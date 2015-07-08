SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Password_bak' 
    ) > 0,
    "SELECT 0",
    "CREATE TABLE Password_bak AS SELECT * FROM Password;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Password' 
          AND column_name='migrated'
    ) > 0,
    "SELECT 0",
    "alter table Password add column migrated boolean not null default false;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;