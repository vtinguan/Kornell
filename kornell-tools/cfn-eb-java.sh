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

TIER="eb"
BRANCH=${BRANCH:-"master"}

ARG_URL="ParameterKey=knljdbcurl,ParameterValue='$JDBC_CONNECTION_STRING'"
ARG_DRIVER="ParameterKey=knljdbcdriver,ParameterValue=$JDBC_DRIVER"
ARG_USERNAME="ParameterKey=knljdbcusername,ParameterValue=$JDBC_USERNAME"
ARG_PASSWORD="ParameterKey=knljdbcpassword,ParameterValue=$JDBC_PASSWORD"

ARG_VPC_ID="ParameterKey=knlvpc,ParameterValue='$VPC_ID'"
ARG_VPC_API_SUBNETS="ParameterKey=knlapisubnetids,ParameterValue='$VPC_API_SUBNETS'"
ARG_VPC_API_SECGS="ParameterKey=knlapisgids,ParameterValue='$VPC_API_SECGS'"

ARG_API_KEYPAIR="ParameterKey=knlkeypair,ParameterValue='$API_KEYPAIR'"
ARG_CERT_ARN="ParameterKey=knlcertificateid,ParameterValue='$CERT_ARN'"

CFN_CREATE_ARGS="--parameters $ARG_VPC_ID $ARG_URL $ARG_DRIVER $ARG_USERNAME $ARG_PASSWORD $ARG_VPC_API_SUBNETS $ARG_VPC_API_SECGS $ARG_API_KEYPAIR $ARG_API_BRANCH $ARG_CERT_ARN"

source $DIR/cfn-create-stack.sh