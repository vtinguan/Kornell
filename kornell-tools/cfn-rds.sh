#!/bin/bash
set -e

BRANCH="database"
TIER="rds"

ARG_VPC_DB_SUBNETS="ParameterKey=knldbsubnetids,ParameterValue='$VPC_DB_SUBNETS'"
ARG_VPC_DB_SECGS="ParameterKey=knldbsgids,ParameterValue='$VPC_DB_SECGS'"
ARG_DB_PUB="ParameterKey=knldbpublic,ParameterValue=$DB_PUB"
ARC_JDBC_PASSWORD="ParameterKey=knldbpassword,ParameterValue='$JDBC_PASSWORD'"

CFN_CREATE_ARGS="--parameters $ARG_VPC_DB_SUBNETS $ARG_VPC_DB_SECGS $ARG_DB_PUB $ARC_JDBC_PASSWORD"

source cfn-create-stack.sh