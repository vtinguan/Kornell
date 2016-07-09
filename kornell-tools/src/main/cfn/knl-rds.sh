#!/bin/bash
set -e
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source $DIR/../bash/bash-utils.sh

ARG_VPC_DB_SUBNETS="ParameterKey=knldbsubnetids,ParameterValue='$VPC_DB_SUBNETS'"
ARG_VPC_DB_SECGS="ParameterKey=knldbsgids,ParameterValue='$VPC_DB_SECGS'"
ARG_DB_PUB="ParameterKey=knldbpublic,ParameterValue=$DB_PUB"
ARG_DB_PASSWORD="ParameterKey=knldbpassword,ParameterValue='$DB_PASSWORD'"
ARG_DB_MULTIAZ="ParameterKey=knldbmultiaz,ParameterValue='$DB_MULTIAZ'"
CFN_CREATE_ARGS="--parameters $ARG_VPC_DB_SUBNETS $ARG_VPC_DB_SECGS $ARG_DB_PUB $ARG_DB_PASSWORD $ARG_DB_MULTIAZ"

source $DIR/cfn-create-stack.sh
