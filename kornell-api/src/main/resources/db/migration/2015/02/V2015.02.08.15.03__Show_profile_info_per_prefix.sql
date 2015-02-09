SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='InstitutionRegistrationPrefix' 
          AND column_name='uuid'
    ) > 0,
    "SELECT 0",
    "ALTER TABLE InstitutionRegistrationPrefix ADD uuid char(36) not null default 'FIX-ME-PLEASE';"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='InstitutionRegistrationPrefix' 
          AND column_name='showEmailOnProfile'
    ) > 0,
    "SELECT 0",
    "ALTER TABLE InstitutionRegistrationPrefix ADD showEmailOnProfile tinyint(1) NOT NULL DEFAULT 1;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='InstitutionRegistrationPrefix' 
          AND column_name='showCPFOnProfile'
    ) > 0,
    "SELECT 0",
    "ALTER TABLE InstitutionRegistrationPrefix ADD showCPFOnProfile tinyint(1) NOT NULL DEFAULT 1;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='InstitutionRegistrationPrefix' 
          AND column_name='showContactInformationOnProfile'
    ) > 0,
    "SELECT 0",
    "ALTER TABLE InstitutionRegistrationPrefix ADD showContactInformationOnProfile tinyint(1) NOT NULL DEFAULT 1;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;


SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='InstitutionRegistrationPrefix' 
          AND column_name='name'
    ) > 0,
    "SELECT 0",
    "ALTER TABLE InstitutionRegistrationPrefix CHANGE prefix name char(36) not null default 'FIX-ME-PLEASE';"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;


SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='CourseClass' 
          AND column_name='institutionRegistrationPrefixUUID'
    ) > 0,
    "SELECT 0",
    "ALTER TABLE CourseClass CHANGE institutionRegistrationPrefix institutionRegistrationPrefixUUID char(36);"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;


SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='CourseClass' 
          AND column_name='registrationType'
    ) > 0,
    "SELECT 0",
    "ALTER TABLE CourseClass CHANGE registrationEnrollmentType registrationType char(36);"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;


SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Person' 
          AND column_name='registrationType'
    ) > 0,
    "SELECT 0",
    "ALTER TABLE Person ADD registrationType char(36) NOT NULL DEFAULT 'FIX-ME-PLEASE';"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;


SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Person' 
          AND column_name='institutionRegistrationPrefixUUID'
    ) > 0,
    "SELECT 0",
    "ALTER TABLE Person ADD institutionRegistrationPrefixUUID char(36);"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
