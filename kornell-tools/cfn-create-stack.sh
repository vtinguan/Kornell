#!/bin/bash
set -e

BRANCH=${BRANCH:-master}
PURPOSE=${PURPOSE:-development}
BUILD=${BUILD:-$(date -u +"%Y%m%d-%H%M")}
STACK_TEMPLATE="`basename "$0" ".sh"`.json"
STACK_NAME=${STACK_NAME:-"knl-$BRANCH-$BUILD-$PURPOSE-$TIER"}
CFN_CREATE_ARGS=${CFN_CREATE_ARGS:-""}

echo "Creating $STACK_NAME from $STACK_TEMPLATE"

echo aws cloudformation create-stack \
	$CFN_CREATE_ARGS \
 	--stack-name $STACK_NAME \
	--template-body file://$STACK_TEMPLATE \
	--query="StackId" \
	--output=text

aws cloudformation create-stack \
	$CFN_CREATE_ARGS \
 	--stack-name $STACK_NAME \
	--template-body file://$STACK_TEMPLATE \
	--query="StackId" \
	--output=text

echo "Waiting for ${STACK_NAME}"
 aws cloudformation wait stack-create-complete \
	--stack-name $STACK_NAME

 aws cloudformation describe-stacks \
	--stack-name ${STACK_NAME} 

echo "Create complete ${STACK_NAME}"	