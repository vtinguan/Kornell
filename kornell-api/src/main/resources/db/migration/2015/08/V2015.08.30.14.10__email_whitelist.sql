SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='Institution' 
          AND column_name='useEmailWhitelist'
    ) > 0,
    "SELECT 0",
    "alter table Institution add column useEmailWhitelist tinyint(1) not null default 0;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;

CREATE TABLE IF NOT EXISTS InstitutionEmailWhitelist (
	uuid char(36) primary key,
	institutionUUID char(36) not null,
	domain char(255) not null,
	foreign key fk_institutionemailwhitelist_institutionuuid (institutionUUID) references Institution (uuid)
)