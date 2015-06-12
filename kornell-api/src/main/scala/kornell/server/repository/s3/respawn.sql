drop database ebdb;
create database ebdb;
use ebdb;
source ~/.eduvem/restore/eduvem-prod-db.latest.sql;
-- run migrations
source ~/Google Drive/Customers/Yassaka/yassaka.sql;
source ~/Google Drive/DevOps/SQL/fixes/2015-06-06-2129__bye_bye_sa-east-1.sql;

UPDATE `ebdb`.`CourseVersion` SET `distributionPrefix`='module1/v0.2' WHERE `uuid`='fb931ed1-dbdc-11e4-84d1-00ff3b62bf45';
UPDATE `ebdb`.`CourseVersion` SET `distributionPrefix`='module4/v0.2' WHERE `uuid`='f6c63382-dbdc-11e4-84d1-00ff3b62bf45';
UPDATE `ebdb`.`CourseVersion` SET `distributionPrefix`='module3/v0.2' WHERE `uuid`='f087d528-dbdc-11e4-84d1-00ff3b62bf45';
UPDATE `ebdb`.`CourseVersion` SET `distributionPrefix`='module2/v0.2' WHERE `uuid`='e16807eb-dbdc-11e4-84d1-00ff3b62bf45';
UPDATE `ebdb`.`CourseVersion` SET `distributionPrefix`='dashboard/v0.2' WHERE `uuid`='d65ab8e3-dbdb-11e4-84d1-00ff3b62bf45';
UPDATE `ebdb`.`CourseVersion` SET `distributionPrefix`='espinafre/v0.2' WHERE `uuid`='826882ea-dbdd-11e4-84d1-00ff3b62bf45';


select 'done!';
