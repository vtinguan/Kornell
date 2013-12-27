#!/bin/bash

#
#KNL_API_URL="http://eduvem-api-test-web.elasticbeanstalk.com/"
#KNL_BUCKET="test.eduvem.com"

KNL_API_URL="http://eduvem-api-prod-web.elasticbeanstalk.com/"
KNL_BUCKET="eduvem.com"

AWS_ACCESS_KEY_ID="AKIAJQP3BHOB4EVVTS4Q"
AWS_SECRET_KEY="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"

S3CMD_CFG=".s3cfg-kornell"
KNL_CFG="./kornell-gwt/target/kornell-gwt-0.0.1-SNAPSHOT/KornellConfig.js"

echo Generating s3cmd config file
echo -ne "access_key = $AWS_ACCESS_KEY_ID
secret_key = $AWS_SECRET_KEY
" > $S3CMD_CFG

echo Setting API Endpoint
echo -n 'KornellConfig.apiEndpoint = "' > $KNL_CFG
echo -n $KNL_API_URL >> $KNL_CFG
echo -n '";' >> $KNL_CFG

echo Deploying Cacheable Files to S3
# Cacheable for 30 days (2592000 seconds)
./tools/s3cmd/s3cmd -c $S3CMD_CFG  \
  --delete-removed \
  --exclude='WEB-INF/*' \
  --exclude='*.nocache.js' \
  --recursive \
  --add-header "Cache-control: max-age=2592000" \
  sync ./kornell-gwt/target/kornell-gwt-0.0.1-SNAPSHOT/  s3://$KNL_BUCKET

echo Deploying Non-cacheable Files to S3
# Don't cache Kornell.nocache.js
./tools/s3cmd/s3cmd -c $S3CMD_CFG  \
  --add-header "Cache-control: max-age=0" \
  put ./kornell-gwt/target/kornell-gwt-0.0.1-SNAPSHOT/Kornell/Kornell.nocache.js  s3://$KNL_BUCKET/Kornell/

echo Unless you see errors above, you should be deployed and good to go. 
