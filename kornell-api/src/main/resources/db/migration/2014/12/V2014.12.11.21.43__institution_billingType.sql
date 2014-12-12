SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Institution' 
          AND column_name='billingType'
    ) > 0,
    "SELECT 0",
    "alter table Institution add column billingType char(36) not null;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;