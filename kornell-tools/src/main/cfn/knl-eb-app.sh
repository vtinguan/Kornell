#!/bin/bash
set -e
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source $DIR/../bash/bash-utils.sh

export API_APP_NAME=${API_APP_NAME:-kornell-api}

ARG_API_APP_NAME="ParameterKey=knlapiappname,ParameterValue='$API_APP_NAME'"

CFN_CREATE_ARGS="--parameters $ARG_API_APP_NAME"

source $DIR/cfn-create-stack.sh
