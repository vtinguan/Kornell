SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Institution' 
          AND column_name='timeZone'
    ) > 0,
    "SELECT 0",
    "alter table Institution add column timeZone character varying(50);"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
update Institution set timeZone = 'America/Sao_Paulo'