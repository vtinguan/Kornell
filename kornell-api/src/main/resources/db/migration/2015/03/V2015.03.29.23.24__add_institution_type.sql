SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Institution' 
          AND column_name='institutionType'
    ) > 0,
    "SELECT 0",
    "alter table Institution add column institutionType char(36) not null;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Institution' 
          AND column_name='dashboardVersionUUID'
    ) > 0,
    "SELECT 0",
    "alter table Institution add column dashboardVersionUUID char(36);"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;

update Institution set institutionType = 'DEFAULT';