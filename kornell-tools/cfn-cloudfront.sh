#!/bin/bash
set -e
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source $DIR/bash-utils.sh

BRANCH=${BRANCH:-"cdn"}
TIER="cloudfront"
DOMAIN_NAME=${DOMAIN_NAME:-"test.eduvem.com"}
CONTENT_BUCKET=${CONTENT_BUCKET:-"us-east-1.CONTENT_BUCKET"}

ARG_S3_DOMAIN_NAME="ParameterKey=knldomainname,ParameterValue='$DOMAIN_NAME'"
ARG_S3_GWT_BUCKET="ParameterKey=knlgwtbucketname,ParameterValue='$S3_GWT_BUCKET'"
ARG_API_ENDPOINT="ParameterKey=knlapiendpoint,ParameterValue='$API_ENDPOINT'"
ARG_CONTENT_BUCKET="ParameterKey=knlcontentbucketname,ParameterValue='$S3_CONTENT_BUCKET'"

CFN_CREATE_ARGS="--parameters $ARG_S3_DOMAIN_NAME $ARG_S3_GWT_BUCKET $ARG_API_ENDPOINT $ARG_CONTENT_BUCKET"

#echo $CFN_CREATE_ARGS
source $DIR/cfn-create-stack.sh