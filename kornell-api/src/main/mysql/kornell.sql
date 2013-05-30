drop database if exists ebdb;
create database ebdb;
grant all on *.* to kornell@'localhost' identified by '42kornell73';
grant all on ebdb.* to kornell@'localhost' identified by '42kornell73';
grant all on ebdb.* to kornell@'%' identified by '42kornell73';