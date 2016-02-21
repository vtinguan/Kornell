SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='S3ContentRepository' 
          AND column_name='distributionURL'
    ) = 0,
    "SELECT 0",
    "alter table S3ContentRepository drop column distributionURL;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;