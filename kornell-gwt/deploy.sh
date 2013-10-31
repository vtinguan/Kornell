#!/bin/bash

echo Generating s3cmd config file
echo "
access_key = $AWS_ACCESS_KEY_ID
secret_key = $AWS_SECRET_KEY
" > s3cfg

echo Setting API Endpoint...
echo 'KornellConfig.apiEndpoint = "http://kornell-api-red.elasticbeanstalk.com";' > ./kornell-gwt/target/kornell-gwt-0.0.1-SNAPSHOT/KornellConfig.js
echo Deploying to S3...
./tools/s3cmd/s3cmd -c ./s3cfg  --delete-removed --exclude="WEB-INF/*" --recursive sync ./kornell-gwt/target/kornell-gwt-0.0.1-SNAPSHOT/  s3://eduvem.com.br
echo Deployed, you should be good to go.

