SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='ContentRepository' 
          AND column_name='prefix'
    ) > 0,
    "SELECT 0",
    "alter table ContentRepository add column accessKeyId character varying(32),
	 add column secretAccessKey character varying(64), add column bucketName character varying(1024),
	 add column prefix character varying(1024), add column region character varying(32),
     add column path character varying(255), add column institutionUUID character varying(36);"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;

update ContentRepository cr inner join S3ContentRepository s3 on cr.uuid = s3.uuid
set cr.accessKeyId = s3.accessKeyId, cr.secretAccessKey = s3.secretAccessKey, 
cr.bucketName = s3.bucketName, cr.prefix = s3.prefix, cr.region = s3.region,
cr.institutionUUID = s3.institutionUUID;

update ContentRepository cr inner join FSContentRepository fs on cr.uuid = fs.uuid
set cr.prefix = fs.prefix, cr.path = fs.path, cr.institutionUUID = fs.institutionUUID;