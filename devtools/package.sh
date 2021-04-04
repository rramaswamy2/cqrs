#!/usr/bin/env bash
#
##
## USAGE: __PROG__
##
## This __PROG__ script can be used to package the code as jar, rpm, deb
##
## __PROG__ has a dependency on FPM. See README.md for more information.
##
## __PROG__ <module>
##
## module:
## - api
## - projections
##
## Examples:
##   __PROG__ api
##   __PROG__ projections

function usage () {
    grep '^##' "$0" | sed -e 's/^##//' -e "s/__PROG__/$me/" 1>&2
}

command -v fpm >/dev/null 2>&1
if [ "$?" -ne "0" ]; then
  usage
  exit 1
fi

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd ${DIR}/../

INPUT_DIR_NAME="temp_input"
OUTPUT_DIR_NAME="build/package"
ARCHITECTURE="all"
OS="linux"

rm -rf ${INPUT_DIR_NAME}
mkdir -p ${INPUT_DIR_NAME}
mkdir -p ${OUTPUT_DIR_NAME}

mkdir -p ${INPUT_DIR_NAME}/opt/omnius/order
mkdir -p ${INPUT_DIR_NAME}/var/log/omnius/order/api
mkdir -p ${INPUT_DIR_NAME}/var/log/omnius/order/projections
case $1 in
    "api"|"projections")
        BUILD_NAME="order-$1"
        ;;
     *)
        usage
        echo "Cannot package that! Use \"api\" or \"projections\" as the first argument!"
        exit 1
        ;;
esac

cp ${1}/build/libs/${1}-*-boot.jar ${INPUT_DIR_NAME}/opt/omnius/order/${1}.jar
if [ "$?" -ne "0" ]; then
  echo "${1} JAR file was not found."
  exit 1
fi

JAR="$(ls -t ./${1}/build/libs/ | head -n1)"
JAR_VERSION=$(echo $JAR | sed -e "s/${1}-//g" -e 's/.jar.*//g')

echo "Creating package for version: ${JAR_VERSION}"

mkdir -p ${INPUT_DIR_NAME}/etc/omnius/order
mkdir -p ${INPUT_DIR_NAME}/etc/logrotate.d
cp package/${1}.properties ${INPUT_DIR_NAME}/etc/omnius/order/${1}.properties
cp ${1}/log4j2.xml ${INPUT_DIR_NAME}/etc/omnius/order/${1}-log4j2.xml
cp package/order-${1}.conf ${INPUT_DIR_NAME}/etc/logrotate.d/order-${1}.conf

install_as_systemd()
{
    mkdir -p ${INPUT_DIR_NAME}/usr/sbin
    mkdir -p ${INPUT_DIR_NAME}/etc/systemd/system
    cp package/systemd/order-${1} ${INPUT_DIR_NAME}/usr/sbin/order-${1}
    cp package/systemd/order-${1}.service ${INPUT_DIR_NAME}/etc/systemd/system/order-${1}.service
    chmod +x ${INPUT_DIR_NAME}/usr/sbin/order-${1}
    chmod 664 ${INPUT_DIR_NAME}/etc/systemd/system/order-${1}.service
}

install_as_initd()
{
    mkdir -p ${INPUT_DIR_NAME}/etc/init.d
    cp package/initd/order-${1} ${INPUT_DIR_NAME}/etc/init.d/order-${1}
    chmod +x ${INPUT_DIR_NAME}/etc/init.d/order-${1}
}

case $2 in
    "systemd")
        install_as_systemd $1
        ;;
    "initd")
        install_as_initd $1
        ;;
    *)
        install_as_systemd $1
        install_as_initd $1
        ;;
esac

fpm -s dir -t deb -n ${BUILD_NAME} -v ${JAR_VERSION} -a ${ARCHITECTURE} -p ${OUTPUT_DIR_NAME} --before-install ./devtools/pre_install.sh --after-install ./devtools/post_install.sh -f ${INPUT_DIR_NAME}/=/
fpm -s dir -t rpm -n ${BUILD_NAME} -v ${JAR_VERSION} -a ${ARCHITECTURE} --rpm-os ${OS} -p ${OUTPUT_DIR_NAME} --before-install ./devtools/pre_install.sh --after-install ./devtools/post_install.sh -f ${INPUT_DIR_NAME}/=/

rm -rf ${INPUT_DIR_NAME}
