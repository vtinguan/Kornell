#!/bin/bash
set -e
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source $DIR/bash-utils.sh

BRANCH=${BRANCH:-"cdn"}
PURPOSE="$INSTITUTION"
DOMAIN_NAME=${DOMAIN_NAME:-"$PURPOSE-$RANDOM.eduvem.com"}

demmand "DOMAIN_NAME"
demmand "S3_GWT_BUCKET"
demmand "S3_LOGS_BUCKET"
demmand "S3_REPOSITORY_BUCKET"
demmand "S3_USERCONTENT_BUCKET"
demmand "API_ENDPOINT"
demmand "CERT_ID"
demmand "INSTITUTION"


ARG_S3_DOMAIN_NAME="ParameterKey=knldomainname,ParameterValue='$DOMAIN_NAME'"
ARG_S3_GWT_BUCKET="ParameterKey=knlgwtbucketname,ParameterValue='$S3_GWT_BUCKET'"
ARG_S3_LOGS_BUCKET="ParameterKey=knllogsbucketname,ParameterValue='$S3_LOGS_BUCKET'"
ARG_REPOSITORY_BUCKET="ParameterKey=knlrepositorybucketname,ParameterValue='$S3_REPOSITORY_BUCKET'"
ARG_USERCONTENT_BUCKET="ParameterKey=knlusercontentbucketname,ParameterValue='$S3_USERCONTENT_BUCKET'"
ARG_API_ENDPOINT="ParameterKey=knlapiendpoint,ParameterValue='$API_ENDPOINT'"
ARG_CERT_ID="ParameterKey=knlcertificateid,ParameterValue='$CERT_ID'"


CFN_CREATE_ARGS="--parameters $ARG_S3_DOMAIN_NAME $ARG_S3_GWT_BUCKET $ARG_S3_LOGS_BUCKET $ARG_API_ENDPOINT $ARG_USERCONTENT_BUCKET $ARG_REPOSITORY_BUCKET $ARG_CERT_ID"

#echo $CFN_CREATE_ARGS
source $DIR/cfn-create-stack.sh