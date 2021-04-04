#!/bin/bash
#
##
## USAGE: __PROG__
##
## This __PROG__ script can be used to build the docker image
##
## __PROG__ <module> <image_name>
##
## module:
## - api
## - projections
##
## Example:
##   __PROG__ api dockerhub.dynacommercelab.com/omnius-vnext/order-api

SCRIPTPATH=$(dirname "$(readlink -f "$0")")
me=`basename "$0"`

function usage () {
    grep '^##' "$0" | sed -e 's/^##//' -e "s/__PROG__/$me/" 1>&2
}

if [ "$#" -ne 2 ] ; then
    usage
    exit 1
fi

set +x

CQRS_PROJ_VERSION=$(grep '^ *version' build.gradle | sed "s/^ *version = '\(.*\)'.*/\1/")
MODULE=$1
DOCKER_IMAGE=$2

docker build --build-arg JAR_VERSION="${CQRS_PROJ_VERSION}" -t "${DOCKER_IMAGE}" "${MODULE}"
docker tag "${DOCKER_IMAGE}" "${DOCKER_IMAGE}:${CQRS_PROJ_VERSION}"