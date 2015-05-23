drop database if exists ebdb; 
create database if not exists ebdb;
  
grant all on ebdb.* to kornell@'localhost' identified by '_CHANGE_ME_';
grant all on ebdb.* to kornell@'%' identified by '_CHANGE_ME_';
  