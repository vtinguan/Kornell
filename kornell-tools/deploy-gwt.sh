#!/bin/bash
set -e
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source $DIR/bash-utils.sh


REGION=${REGION:-"us-east-1"}
SRC_DIR=${SRC_DIR:-"$DIR/../kornell-gwt/target/kornell-gwt"}

demmand "S3_GWT_BUCKET"
demmand "REGION"
demmand "SRC_DIR"

log "[$0] Deploying cacheable files to S3"

aws s3 sync \
  ${SRC_DIR}/ \
  s3://$S3_GWT_BUCKET \
  --delete \
  --exclude='*.nocache.*' \
  --exclude='WEB-INF/*' \
  --cache-control 'max-age=2592000' \
  --region=$REGION

log "Deploying non-cacheable Files to S3"

aws s3 cp \
  ${SRC_DIR}/ \
  s3://$S3_GWT_BUCKET \
  --recursive \
  --exclude '*' \
  --include '*.nocache.*' \
  --cache-control 'max-age=0' \
  --region=$REGION

log "GWT module deployment finished successfully"