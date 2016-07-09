#!/bin/bash
set -e
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source $DIR/../bash/bash-utils.sh

source $DIR/deploy-gwt.sh
source $DIR/deploy-api.sh

echo "AWS deployment successfull"