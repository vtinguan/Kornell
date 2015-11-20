SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='FSContentRepository' 
          AND column_name='institutionUUID'
    ) > 0,
    "SELECT 0",
    "alter table FSContentRepository add column institutionUUID character varying(36) not null;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;