#!/bin/bash

function suicide() {
	echo $1 >&2
	exit
}

if [[ -z "$KNL_API_URL" ]]; 
	then suicide "Set KNL_API_URL to the url of the API environment."; fi
if [ -z "$KNL_BUCKET" ]; 
	then suicide "Set KNL_BUCKET to the name of the deployment bucket"; fi
if [ -z "$AWS_ACCESS_KEY_ID" ]; 
	then suicide "Set AWS_ACCESS_KEY_ID to your AWS access key"; fi
if [ -z "$AWS_SECRET_ACCESS_KEY" ]; 
	then suicide "Set AWS_SECRET_ACCESS_KEY to your AWS secret key"; fi

S3CMD=${S3CMD:-"s3cmd"}
S3CMD_CFG=${S3CMD_CFG:-".s3cfg-kornell"} 
SRC_DIR=${SRC_DIR:-"${PWD}/kornell-gwt/target/kornell-gwt"}
KNL_CFG=${KNL_CFG:-$SRC_DIR"/KornellConfig.js"}


echo "=== GWT Deployment Variables ==="
echo "KNL_API_URL=$KNL_API_URL"
echo "KNL_BUCKET=$KNL_BUCKET"
echo "S3CMD=$S3CMD"
echo "S3CMD_CFG=$S3CMD_CFG"
echo "KNL_CFG=$KNL_CFG"
echo "AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID"
echo "================================"

echo Generating s3cmd config file
echo -ne "access_key = $AWS_ACCESS_KEY_ID
secret_key = $AWS_SECRET_ACCESS_KEY
" > $S3CMD_CFG

echo Setting API Endpoint
echo -n 'KornellConfig.apiEndpoint = "' > $KNL_CFG
echo -n $KNL_API_URL >> $KNL_CFG
echo -n '";' >> $KNL_CFG

echo Deploying Cacheable Files to S3
# Cacheable for 30 days (2592000 seconds)
$S3CMD -c $S3CMD_CFG  \
  --delete-removed \
  --exclude='WEB-INF/*' \
  --exclude='*.nocache.js' \
  --recursive \
  --add-header "Cache-control: max-age=2592000" \
  sync ${SRC_DIR}/  s3://$KNL_BUCKET

echo Deploying Non-cacheable Files to S3
# Don't cache Kornell.nocache.js
$S3CMD -c $S3CMD_CFG  \
  --add-header "Cache-control: max-age=0" \
  put ${SRC_DIR}/Kornell/Kornell.nocache.js  s3://$KNL_BUCKET/Kornell/

echo Unless you see errors above, you should be deployed and good to go. 
