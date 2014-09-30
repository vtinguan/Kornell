SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Course' 
          AND column_name='institutionUUID'
    ) > 0,
    "SELECT 0",
    "ALTER TABLE Course ADD institutionUUID CHAR(36) NOT NULL DEFAULT 'FIX-ME';"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Course' 
          AND column_name='institutionUUID'
    ) < 1,
    "SELECT 0",
    "UPDATE Course c, Curriculum r
      SET    c.institutionUUID = r.institutionUUID
      WHERE  c.UUID = r.courseUUID;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;

DROP TABLE IF EXISTS Curriculum;