#!/usr/bin/env bash

##
## USAGE: __PROG__
##
## This __PROG__ script can be used to upload the linux packages to nexus.
##
## Usage example:
##   ./__PROG__ api deb "${NEXUS_CRED_USR}:${NEXUS_CRED_PSW}"
##   ./__PROG__ projections deb "${NEXUS_CRED_USR}:${NEXUS_CRED_PSW}"
##   ./__PROG__ api rpm "${NEXUS_CRED_USR}:${NEXUS_CRED_PSW}"
##   ./__PROG__ projections rpm "${NEXUS_CRED_USR}:${NEXUS_CRED_PSW}"
##

SCRIPTPATH=$(dirname "$(readlink -f "$0")")
me=`basename "$0"`

function usage () {
    grep '^##' "$0" | sed -e 's/^##//' -e "s/__PROG__/$me/" 1>&2
}

if [ "$1" = '-h' ] ; then
    usage
    exit 0
fi

if [ $# -ne 3 ] ; then
    usage
    exit 1
fi

NEXUS=https://nexus.dynacommercelab.com
FILE_PATH=$(ls $SCRIPTPATH/../build/package/*${1}*.${2} | head -n1)
PACKAGE_NAME=$(basename $FILE_PATH)
curl -v --user ${3} --upload-file $SCRIPTPATH/../build/package/${PACKAGE_NAME} ${NEXUS}/repository/${2}-packages/order/${PACKAGE_NAME}
