#!/bin/bash
set -e

BRANCH="cdn"
TIER="cloudfront"
DOMAIN_NAME="test.eduvem.com"

ARG_S3_GWT_BUCKET="ParameterKey=knlgwtbucketname,ParameterValue='$S3_GWT_BUCKET'"

source cfn-create-stack.sh