#!/bin/bash

function suicide() {
	echo $1 >&2
	exit
}

function log(){
  echo "[$0] $1"
}

if [[ -z "$KNL_API_URL" ]]; 
	then suicide "Set KNL_API_URL to the url of the API environment."; fi
if [ -z "$KNL_BUCKET" ]; 
	then suicide "Set KNL_BUCKET to the name of the deployment bucket"; fi
if [ -z "$AWS_ACCESS_KEY_ID" ]; 
	then suicide "Set AWS_ACCESS_KEY_ID to your AWS access key"; fi
if [ -z "$AWS_SECRET_ACCESS_KEY" ]; 
	then suicide "Set AWS_SECRET_ACCESS_KEY to your AWS secret key"; fi

REGION=${REGION:-"sa-east-1"}
AWS_CLI=${AWS_CLI:-"aws"}
S3CMD_CFG=${S3CMD_CFG:-".s3cfg-kornell"} 
SRC_DIR=${SRC_DIR:-"${PWD}/kornell-gwt/target/kornell-gwt"}
KNL_CFG=${KNL_CFG:-$SRC_DIR"/KornellConfig.js"}

log "=== GWT Deployment Variables ==="
log "KNL_API_URL=$KNL_API_URL"
log "KNL_BUCKET=$KNL_BUCKET"
log "S3CMD=$S3CMD"
log "S3CMD_CFG=$S3CMD_CFG"
log "KNL_CFG=$KNL_CFG"
log "AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID"
log "================================"

log "Setting API Endpoint"
echo -n 'KornellConfig.apiEndpoint = "' > $KNL_CFG
echo -n $KNL_API_URL >> $KNL_CFG
echo -n '";' >> $KNL_CFG

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
