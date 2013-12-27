#!/bin/bash

TARGET_HOST=localhost
TARGET_USER=kornell
TARGET_PASS=42kornell73
TARGET_DB=ebdb

SRC_HOST=eduvem-db-test.clogtolxeywz.sa-east-1.rds.amazonaws.com
SRC_USER=kornell
SRC_PASS=42kornell73
SRC_DB=ebdb


mysql -h$TARGET_HOST -u$TARGET_USER -p$TARGET_PASS -e 'drop database ebdb; create database ebdb'
mysqldump -h$SRC_HOST -u$SRC_USER -p$SRC_PASS $SRC_DB | mysql -h$TARGET_HOST -u$TARGET_USER -p$TARGET_PASS $TARGET_DB

echo "Game Over. Database should be respawned."