#!/bin/bash
set -e

TIER="app"

ARG_URL="ParameterKey=knljdbcurl,ParameterValue=\"$JDBC_CONNECTION_STRING\""
ARG_DRIVER="ParameterKey=knljdbcdriver,ParameterValue=$JDBC_DRIVER"
ARG_USERNAME="ParameterKey=knljdbcusername,ParameterValue=$JDBC_USERNAME"
ARG_PASSWORD="ParameterKey=knljdbcpassword,ParameterValue=$JDBC_PASSWORD"

ARG_VPC_API_SUBNETS="ParameterKey=knlapisubnetids,ParameterValue='$VPC_API_SUBNETS'"
ARG_VPC_API_SECGS="ParameterKey=knlapisgids,ParameterValue='$VPC_API_SECGS'"

CFN_CREATE_ARGS="--parameters  $ARG_URL $ARG_DRIVER $ARG_USERNAME $ARG_PASSWORD $VPC_API_SUBNETS $VPC_API_SECGS"

source cfn-create-stack.sh