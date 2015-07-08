SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Person' 
          AND column_name='receiveEmailCommunication'
    ) > 0,
    "SELECT 0",
    "ALTER TABLE Person ADD receiveEmailCommunication tinyint(1) NOT NULL DEFAULT 1;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
