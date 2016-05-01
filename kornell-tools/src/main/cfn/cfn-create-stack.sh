#!/bin/bash
set -e
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source $DIR/../bash/bash-utils.sh


BRANCH=${BRANCH:-master}
PURPOSE=${PURPOSE:-dev}
BUILD=${BUILD:-$(date -u +"%Y%m%d%H%M")}
STACK_TEMPLATE="`basename "$0" ".sh"`.json"
STACK_NAME=${STACK_NAME:-"knl-$BRANCH-$BUILD-$PURPOSE"}
CFN_CREATE_ARGS=${CFN_CREATE_ARGS:-""}

echo "Creating $STACK_NAME from $STACK_TEMPLATE"

TEMPLATE_BODY=file://$DIR/$STACK_TEMPLATE

echo aws cloudformation create-stack \
	$CFN_CREATE_ARGS \
 	--stack-name $STACK_NAME \
	--template-body $TEMPLATE_BODY \
	--query="StackId" \
	--output=text

aws cloudformation create-stack \
	$CFN_CREATE_ARGS \
 	--stack-name $STACK_NAME \
	--template-body $TEMPLATE_BODY \
	--query="StackId" \
	--output=text

echo "Waiting for ${STACK_NAME}"
 aws cloudformation wait stack-create-complete \
	--stack-name $STACK_NAME

echo " "
echo "Before creating the next tier, please check and set these vars:"
echo " "
aws cloudformation describe-stacks \
        --stack-name=${STACK_NAME}  \
        --query="Stacks[*].Outputs[*].[Description,OutputValue]" \
        --output=text | tr '\t' '=' | sed -e 's/^/export /'
echo " "
echo "Create complete ${STACK_NAME}"	