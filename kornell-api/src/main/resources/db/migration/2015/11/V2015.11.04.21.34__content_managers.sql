CREATE TABLE IF NOT EXISTS ContentRepository (
  `uuid` char(36) NOT NULL,
  `repositoryType` varchar(255) NOT NULL, 
  PRIMARY KEY (`uuid`)
) AS SELECT uuid, 'S3' as repositoryType from S3ContentRepository;

