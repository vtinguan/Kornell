#!/bin/bash

# Load Environment
source /opt/kornell/kornell.env
export AWS_DEFAULT_REGION

# Setup NewRelic Agent
mkdir -p /opt/newrelic
NRELIC_BIN_URL=s3://dist-sa-east-1.craftware.com/NewRelic/newrelic.jar
NRELIC_BIN_FILE=/opt/newrelic/newrelic.jar
aws s3 cp $NRELIC_BIN_URL $NRELIC_BIN_FILE

NRELIC_YML_FILE=/opt/newrelic/newrelic.yml
aws s3 cp $NRELIC_YML_URL $NRELIC_YML_FILE 

# Download JDBC Driver
JDBC_URL=s3://dist-sa-east-1.craftware.com/MySQL/mysql-connector-java.jar
JDBC_FILE=/opt/wildfly/standalone/deployments/mysql-connector-java.jar
aws s3 cp $JDBC_URL $JDBC_FILE

# Download latest configuration
CFG_URL=s3://dist-sa-east-1.craftware.com/WildFly/standalone-kornell-api.xml
CFG_FILE=/opt/wildfly/standalone/configuration/standalone-kornell-api.xml
aws s3 cp $CFG_URL $CFG_FILE

# Dowload latest application version
KNL_API_URL=s3://dist-sa-east-1.craftware.com/Kornell/kornell-api.war
KNL_API_FILE=/opt/wildfly/standalone/deployments/kornell-api.war
aws s3 cp $KNL_API_URL $KNL_API_FILE

# Start WildFly
export JAVA_OPTS="$JAVA_OPTS -javaagent:$NRELIC_BIN_FILE"
/opt/wildfly/bin/standalone.sh -c standalone-kornell-api.xml \
 -b 0.0.0.0 \
 -Dnewrelic.environment=${NEWRELIC_ENV-"unknown"} \
 -DJNDI_ROOT="java:/" \
 -DJNDI_DATASOURCE="datasources/KornellDS" \
 -Dkornell.api.jdbc.url=$JDBC_CONNECTION_STRING \
 -Dkornell.api.jdbc.driver=$JDBC_DRIVER \
 -Dkornell.api.jdbc.username=$JDBC_USERNAME \
 -Dkornell.api.jdbc.password=$JDBC_PASSWORD &
