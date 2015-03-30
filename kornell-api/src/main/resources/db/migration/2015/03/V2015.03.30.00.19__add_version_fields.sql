SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='CourseVersion' 
          AND column_name='parentVersionUUID'
    ) > 0,
    "SELECT 0",
    "alter table CourseVersion add column parentVersionUUID char(36);"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='CourseVersion' 
          AND column_name='instanceCount'
    ) > 0,
    "SELECT 0",
    "alter table CourseVersion add column instanceCount int default 1;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE
          table_schema=DATABASE() 
          AND table_name='CourseVersion' 
          AND constraint_name='check_instanceCount'
    ) > 0,
    "SELECT 0",
    "alter table CourseVersion ADD CONSTRAINT check_instanceCount CHECK (instanceCount > 0);"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='CourseVersion' 
          AND column_name='label'
    ) > 0,
    "SELECT 0",
    "alter table CourseVersion add column label char(255);"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;