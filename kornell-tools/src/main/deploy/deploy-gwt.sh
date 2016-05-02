#!/bin/bash
set -e
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source $DIR/../bash/bash-utils.sh

export AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID:-"$bamboo_awsAccessKeyId"} 
export AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY:-"$bamboo_awsSecretKeypassword"}
TEMP_DIR=${TEMP_DIR:-"$DIR/kornell-gwt-s3/"}
REGION=${bamboo_REGION:-"us-east-1"}
GWT_ARTIFACT=${bamboo_GWT_ARTIFACT:-"$DIR/../kornell-gwt/target/kornell-gwt-s3.zip"}
S3_GWT_BUCKET=${bamboo_S3_GWT_BUCKET}

demmand "S3_GWT_BUCKET"
demmand "REGION"
demmand "GWT_ARTIFACT"

#TODO: Upload artifact to dist

log "Uncompressing [$GWT_ARTIFACT] to [$TEMP_DIR]"
mkdir -p $TEMP_DIR
unzip -o $GWT_ARTIFACT -d $TEMP_DIR

log "Using access key id [$AWS_ACCESS_KEY_ID]"
log "Deploying cacheable files to S3"
aws s3 sync \
  $TEMP_DIR \
  s3://$S3_GWT_BUCKET \
  --delete \
  --exclude='*.nocache.*' \
  --exclude='WEB-INF/*' \
  --cache-control 'max-age=2592000' \
  --region=$REGION

log "Deploying non-cacheable Files to S3"
aws s3 cp \
  $TEMP_DIR \
  s3://$S3_GWT_BUCKET \
  --recursive \
  --exclude '*' \
  --include '*.nocache.*' \
  --cache-control 'max-age=0' \
  --region=$REGION

log "Cleaning [$TEMP_DIR]"
rm -rf $TEMP_DIR

log "GWT module deployment finished successfully"