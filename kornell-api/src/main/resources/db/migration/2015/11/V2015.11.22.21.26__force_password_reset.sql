SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Person' 
          AND column_name='forcePasswordReset'
    ) > 0,
    "SELECT 0",
    "alter table Person add column forcePasswordReset boolean not null default false;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;