ALTER TABLE S3ContentRepository 
CHANGE COLUMN `accessKeyId` `accessKeyId` VARCHAR(32) NULL ,
CHANGE COLUMN `secretAccessKey` `secretAccessKey` VARCHAR(64) NULL ;