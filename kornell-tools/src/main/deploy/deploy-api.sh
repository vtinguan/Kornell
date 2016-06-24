#!/bin/bash
set -e
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source $DIR/../bash/bash-utils.sh

REGION=${bamboo_REGION:-"us-east-1"}
TEMP_DIR=${TEMP_DIR:-"$DIR/kornell-api-eb/"}
API_ARTIFACT=${bamboo_API_ARTIFACT:-"$DIR/../../../../kornell-api/target/kornell-api-eb.zip"}
APPLICATION_NAME=${bamboo_APPLICATION_NAME:-"$APPLICATION_NAME" }
ENV_NAME=${bamboo_ENV_NAME:-"$ENV_NAME"}
VERSION_ID=${VERSION_ID:-"$(date -u +'%Y%m%d%H%M')"}
VERSION_BUCKET=${VERSION_BUCKET:-"us-east-1.craftware-dist"}
VERSION_BRANCH=${VERSION_BRANCH:-"master"}
VERSION_LABEL=${VERSION_LABEL:-"kornell-api-$VERSION_ID"}
VERSION_KEY=${VERSION_KEY:-"kornell/$VERSION_BRANCH/latest/kornell-api-eb.zip"}
VERSION_URL="s3://$VERSION_BUCKET/$VERSION_KEY"

demmand "REGION"
demmand "API_ARTIFACT"
demmand "APPLICATION_NAME"
demmand "ENV_NAME"

log "Uploading [$API_ARTIFACT] to [$VERSION_URL]"
aws s3 cp $API_ARTIFACT $VERSION_URL

log "Creating application [$APPLICATION_NAME] version [$VERSION_LABEL]"
echo aws elasticbeanstalk create-application-version \
	--application-name "$APPLICATION_NAME" \
	--version-label "$VERSION_LABEL" \
	--source-bundle "S3Bucket=$VERSION_BUCKET,S3Key=$VERSION_KEY" \
	--region "$REGION"

aws elasticbeanstalk create-application-version \
	--application-name "$APPLICATION_NAME" \
	--version-label "$VERSION_LABEL" \
	--source-bundle "S3Bucket=$VERSION_BUCKET,S3Key=$VERSION_KEY" \
	--region "$REGION"	

log "Updating environment [$ENV_NAME]"
echo aws elasticbeanstalk update-environment \
	--application-name "$APPLICATION_NAME" \
	--environment-name "$ENV_NAME" \
	--version-label "$VERSION_LABEL" \
	--region "$REGION"
aws elasticbeanstalk update-environment \
	--application-name "$APPLICATION_NAME" \
	--environment-name "$ENV_NAME" \
	--version-label "$VERSION_LABEL" \
	--region "$REGION"	

log "API module deployment finished successfully"
