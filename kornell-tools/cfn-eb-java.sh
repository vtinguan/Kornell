#!/bin/bash
set -e

TIER="app"

ARG_URL="ParameterKey=knljdbcurl,ParameterValue='$JDBC_CONNECTION_STRING'"
ARG_DRIVER="ParameterKey=knljdbcdriver,ParameterValue=$JDBC_DRIVER"
ARG_USERNAME="ParameterKey=knljdbcusername,ParameterValue=$JDBC_USERNAME"
ARG_PASSWORD="ParameterKey=knljdbcpassword,ParameterValue=$JDBC_PASSWORD"

ARG_VPC_ID="ParameterKey=knlvpc,ParameterValue='$VPC_ID'"
ARG_VPC_API_SUBNETS="ParameterKey=knlapisubnetids,ParameterValue='$VPC_API_SUBNETS'"
ARG_VPC_API_SECGS="ParameterKey=knlapisgids,ParameterValue='$VPC_API_SECGS'"

ARG_API_KEYPAIR="ParameterKey=knlkeypair,ParameterValue='$API_KEYPAIR'"


CFN_CREATE_ARGS="--parameters $ARG_VPC_ID $ARG_URL $ARG_DRIVER $ARG_USERNAME $ARG_PASSWORD $ARG_VPC_API_SUBNETS $ARG_VPC_API_SECGS $ARG_API_KEYPAIR"

source cfn-create-stack.sh