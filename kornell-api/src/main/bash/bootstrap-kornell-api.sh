#!/bin/bash

mkdir -p /opt/kornell/

# Setup Environment
ENV=/opt/kornell/kornell.env
AWS_DEFAULT_REGION='us-east-1'

# Save Environment
echo AWS_DEFAULT_REGION="$AWS_DEFAULT_REGION" > $ENV
echo JDBC_CONNECTION_STRING='' >> $ENV
echo JDBC_DRIVER='' >> $ENV
echo JDBC_USERNAME='' >> $ENV
echo JDBC_PASSWORD='' >> $ENV
echo NRELIC_YML_URL='' >> $ENV

# Download Initialization Script
aws s3 cp s3://dist-us-east-1.craftware.com/Kornell/standalone-kornell-api.sh /opt/kornell/standalone-kornell-api.sh
chmod +x  /opt/kornell/standalone-kornell-api.sh

# Add to system initialization
echo '/opt/kornell/standalone-kornell-api.sh &' >> /etc/rc.d/rc.local