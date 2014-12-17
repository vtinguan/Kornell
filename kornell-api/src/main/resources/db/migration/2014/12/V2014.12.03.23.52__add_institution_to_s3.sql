SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='S3ContentStore' 
          AND column_name='institutionUUID'
    ) > 0,
    "SELECT 0",
    "alter table S3ContentStore add column institutionUUID char(36) not null;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;