#!/bin/bash

function suicide() {
	echo $1 >&2
	exit
}

function log(){
  echo "[$0] $1"
}

if [ -z "$KNL_BUCKET" ]; 
	then suicide "Set KNL_BUCKET to the name of the deployment bucket"; fi

REGION=${REGION:-"sa-east-1"}
AWS_CLI=${AWS_CLI:-"aws"}
SRC_DIR=${SRC_DIR:-"${PWD}/kornell-gwt/target/kornell-gwt"}

log "=== GWT Deployment Variables ==="
log "KNL_BUCKET=$KNL_BUCKET"
log "================================"

log "[$0] Deploying Cacheable Files to S3"

$AWS_CLI s3 sync \
  ${SRC_DIR}/ \
  s3://$KNL_BUCKET \
  --delete \
  --exclude='*.nocache.*' \
  --exclude='WEB-INF/*' \
  --cache-control 'max-age=2592000' \
  --region=$REGION

log "Deploying Non-cacheable Files to S3"

$AWS_CLI s3 cp \
  ${SRC_DIR}/ \
  s3://$KNL_BUCKET \
  --recursive \
  --exclude '*' \
  --include '*.nocache.*' \
  --cache-control 'max-age=0' \
  --region=$REGION

log "GWT component deploying finished"