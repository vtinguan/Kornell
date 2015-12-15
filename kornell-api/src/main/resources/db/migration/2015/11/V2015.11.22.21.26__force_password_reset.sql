SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Person' 
          AND column_name='forcePasswordUpdate'
    ) > 0,
    "SELECT 0",
    "alter table Person add column forcePasswordUpdate boolean not null default false;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;