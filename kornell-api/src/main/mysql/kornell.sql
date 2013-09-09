drop database if exists ebdb; 
create database if not exists ebdb default charset utf8;
use ebdb;
  
grant all on ebdb.* to kornell@'localhost' identified by '42kornell73';
grant all on ebdb.* to kornell@'%' identified by '42kornell73';
  