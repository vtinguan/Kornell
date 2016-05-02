#!/bin/bash
set -e
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source $DIR/../bash/bash-utils.sh

TEMP_DIR=${TEMP_DIR:-"$DIR/kornell-gwt-s3/"}
REGION=${REGION:-"us-east-1"}
GWT_ARTIFACT=${GWT_ARTIFACT:-"$DIR/../kornell-gwt/target/kornell-gwt-s3.zip"}

demmand "S3_GWT_BUCKET"
demmand "REGION"
demmand "GWT_ARTIFACT"

#TODO: Upload artifact to dist

log "Uncompressing [$GWT_ARTIFACT] to [$TEMP_DIR]"
mkdir -p $TEMP_DIR
unzip -o $GWT_ARTIFACT -d $TEMP_DIR

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