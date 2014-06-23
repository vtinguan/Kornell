#!/bin/bash

mkdir -p /opt/kornell/

# Setup Environment
ENV=/opt/kornell/kornell.env
echo JDBC_CONNECTION_STRING='"jdbc:mysql://eduvem-db-test.clogtolxeywz.sa-east-1.rds.amazonaws.com:3306/ebdb?useUnicode=true&amp;characterEncoding=utf8"' > $ENV
echo JDBC_DRIVER='"mysql-connector-java.jar_com.mysql.jdbc.Driver_5_1"' >> $ENV
echo JDBC_USERNAME='"kornell"' >> $ENV
echo JDBC_PASSWORD='"42kornell73"' >> $ENV

# Download Initialization Script
curl -s -o /opt/kornell/standalone-kornell-api.sh https://s3-sa-east-1.amazonaws.com/dist-sa-east-1.craftware.com/Kornell/standalone-kornell-api.sh
chmod +x  /opt/kornell/standalone-kornell-api.sh

# Add to system initialization
echo '/opt/kornell/standalone-kornell-api.sh &' >> /etc/rc.d/rc.local