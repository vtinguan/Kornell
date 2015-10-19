DROP TABLE IF EXISTS `EntityChanged`;
CREATE TABLE IF NOT EXISTS `EntityChanged` (
  `uuid` char(36) NOT NULL,
  `eventFiredAt` char(29) DEFAULT NULL,
  `personUUID` char(36),
  `entityType` char(36) NOT NULL DEFAULT 'FIXME-PLS',
  `institutionUUID` char(36) NOT NULL,
  `entityUUID` char(36) NOT NULL,
  `fromValue` text,
  `toValue` text,
  PRIMARY KEY (`uuid`),
  KEY `fk_entitychanged_person_personUUID_idx` (`personUUID`)
);