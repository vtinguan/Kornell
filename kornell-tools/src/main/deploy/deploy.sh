#!/bin/bash
set -e
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

if [ -n "$bamboo_awsAccessKeyId" ]; then
	export AWS_ACCESS_KEY_ID=$bamboo_awsAccessKeyId
	export AWS_SECRET_ACCESS_KEY=$bamboo_awsAccessKeyId	
	log "Using access key id [$AWS_ACCESS_KEY_ID]"
fi 

source $DIR/deploy-gwt.sh
source $DIR/deploy-api.sh

echo "AWS deployment successfull"