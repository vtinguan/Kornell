SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='CourseClass' 
          AND column_name='allowBatchCancellation'
    ) > 0,
    "SELECT 0",
    "ALTER TABLE CourseClass ADD allowBatchCancellation tinyint(1) NOT NULL DEFAULT 0;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;