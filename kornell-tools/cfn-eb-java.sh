#!/bin/bash
set -e
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source $DIR/bash-utils.sh


demmand "JDBC_CONNECTION_STRING"
demmand "JDBC_DRIVER"
demmand "JDBC_USERNAME"
demmand "JDBC_PASSWORD"
demmand "VPC_ID"
demmand "VPC_API_SUBNETS"
demmand "VPC_API_SECGS"
demmand "API_KEYPAIR"
demmand "CERT_ARN"

BRANCH=${BRANCH:-"master"}

ARG_URL="ParameterKey=knljdbcurl,ParameterValue='$JDBC_CONNECTION_STRING'"
ARG_DRIVER="ParameterKey=knljdbcdriver,ParameterValue=$JDBC_DRIVER"
ARG_USERNAME="ParameterKey=knljdbcusername,ParameterValue=$JDBC_USERNAME"
ARG_PASSWORD="ParameterKey=knljdbcpassword,ParameterValue=$JDBC_PASSWORD"

ARG_SMTP_HOST="ParameterKey=knlsmtphost,ParameterValue='$SMTP_HOST'"
ARG_SMTP_PORT="ParameterKey=knlsmtpport,ParameterValue='$SMTP_HOST'"
ARG_SMTP_USERNAME="ParameterKey=knlsmtpusername,ParameterValue='$SMTP_USERNAME'"
ARG_SMTP_PASSWORD="ParameterKey=knlsmtppassword,ParameterValue='$SMTP_PASSWORD'"
ARG_REPLY_TO="ParameterKey=knlreplyto,ParameterValue='$REPLY_TO'"
ARG_USER_CONTENT_BUCKET="ParameterKey=knlusercontentbucketname,ParameterValue='$USER_CONTENT_BUCKET'"

ARG_VPC_ID="ParameterKey=knlvpc,ParameterValue='$VPC_ID'"
ARG_VPC_API_SUBNETS="ParameterKey=knlapisubnetids,ParameterValue='$VPC_API_SUBNETS'"
ARG_VPC_API_SECGS="ParameterKey=knlapisgids,ParameterValue='$VPC_API_SECGS'"

ARG_API_KEYPAIR="ParameterKey=knlkeypair,ParameterValue='$API_KEYPAIR'"
ARG_CERT_ARN="ParameterKey=knlcertificateid,ParameterValue='$CERT_ARN'"

CFN_CREATE_ARGS="--parameters $ARG_VPC_ID $ARG_URL $ARG_DRIVER $ARG_USERNAME $ARG_PASSWORD $ARG_VPC_API_SUBNETS $ARG_VPC_API_SECGS $ARG_API_KEYPAIR $ARG_API_BRANCH $ARG_CERT_ARN"
CFN_CREATE_ARGS="$CFN_CREATE_ARGS $ARG_SMTP_HOST $ARG_SMTP_PORT $ARG_SMTP_USERNAME $ARG_SMTP_PASSWORD $ARG_REPLY_TO $ARG_USER_CONTENT_BUCKET"

source $DIR/cfn-create-stack.sh