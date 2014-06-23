#!/bin/bash

# Load Environment
source /opt/kornell/kornell.env

# Download JDBC Driver
JDBC_URL=https://s3-sa-east-1.amazonaws.com/dist-sa-east-1.craftware.com/MySQL/mysql-connector-java.jar
JDBC_FILE=/opt/wildfly/standalone/deployments/mysql-connector-java.jar
curl -s -o $JDBC_FILE $JDBC_URL

# Download latest configuration
CFG_URL=https://s3-sa-east-1.amazonaws.com/dist-sa-east-1.craftware.com/WildFly/standalone-kornell-api.xml
CFG_FILE=/opt/wildfly/standalone/configuration/standalone-kornell-api.xml
curl -s -o $CFG_FILE $CFG_URL

# Dowload latest application version
KNL_API_URL=https://s3-sa-east-1.amazonaws.com/dist-sa-east-1.craftware.com/Kornell/kornell-api.war
KNL_API_FILE=/opt/wildfly/standalone/deployments/kornell-api.war
curl -s -o $KNL_API_FILE $KNL_API_URL 

# Start WildFly
/opt/wildfly/bin/standalone.sh -c standalone-kornell-api.xml \
 -b 0.0.0.0 \
 -DJNDI_ROOT="java:/" \
 -DJNDI_DATASOURCE="datasources/KornellDS" \
 -Dkornell.api.jdbc.url=$JDBC_CONNECTION_STRING \
 -Dkornell.api.jdbc.driver=$JDBC_DRIVER \
 -Dkornell.api.jdbc.username=$JDBC_USERNAME \
 -Dkornell.api.jdbc.password=$JDBC_PASSWORD &
