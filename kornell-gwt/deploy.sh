#!/bin/bash

echo Generating s3cmd config file
echo "
access_key = $AWS_ACCESS_KEY_ID
secret_key = $AWS_SECRET_KEY
" > s3cfg

echo Setting API Endpoint
echo 'KornellConfig.apiEndpoint = "http://kornell-api-red.elasticbeanstalk.com";' > ./kornell-gwt/target/kornell-gwt-0.0.1-SNAPSHOT/KornellConfig.js
echo Deploying to S3
# Cacheable for 30 days (2592000 seconds)
./tools/s3cmd/s3cmd -c ./s3cfg  \
  --delete-removed \
  --exclude='WEB-INF/*' \
  --exclude='*.nocache.js' \
  --recursive \
  --add-header "Cache-control: max-age=2592000" \
  sync ./kornell-gwt/target/kornell-gwt-0.0.1-SNAPSHOT/  s3://eduvem.com

# Don't cache Kornell.nocache.js
./tools/s3cmd/s3cmd -c ./s3cfg  \
  --add-header "Cache-control: max-age=0" \
  put ./kornell-gwt/target/kornell-gwt-0.0.1-SNAPSHOT/Kornell/Kornell.nocache.js  s3://eduvem.com/Kornell

echo Unless you see errors above, you should be deployed and good to go. 
