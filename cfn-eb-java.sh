#!/bin/bash
set -e

TIER="app"

ARG_URL="ParameterKey=knljdbcurl,ParameterValue=\"$JDBC_CONNECTION_STRING\""
ARG_DRIVER="ParameterKey=knljdbcdriver,ParameterValue=$JDBC_DRIVER"
ARG_USERNAME="ParameterKey=knljdbcusername,ParameterValue=$JDBC_USERNAME"
ARG_PASSWORD="ParameterKey=knljdbcpassword,ParameterValue=$JDBC_PASSWORD"
ARG_DB_VPC="ParameterKey=knldbvpcid,ParameterValue=$DB_VPC_ID"


CFN_CREATE_ARGS="--parameters  $ARG_URL $ARG_DRIVER $ARG_USERNAME $ARG_PASSWORD $ARG_DB_VPC"

source cfn-create-stack.sh