#!/bin/sh

#if [ "$#" -lt  "6" ]
# then
#  HOST="10.133.98.115"
#  USER_NAME="admin"
#  PASSWORD="adminpw"
#  ONAP_HOST="10.0.117.22"
#  PORT="30227"
#  TOPIC="unauthenticated.SEC_FAULT_OUTPUT"
#else
#  HOST="$1"
#  USER_NAME="$2"
#  PASSWORD="$3"
#  ONAP_HOST="$4"
#  PORT="$5"
#  TOPIC="$6"
#fi


PROPS="-DMCP_HOST=${HOST}" 
PROPS="${PROPS} -DMCP_USER_NAME=${USER_NAME}" 
PROPS="${PROPS} -DMCP_PW=${PASSWORD}" 
PROPS="${PROPS} -DONAP_HOST=${ONAP_HOST}"
PROPS="${PROPS} -DONAP_DMAAP_PORT=${PORT}" 
PROPS="${PROPS} -DONAP_DMAAP_TOPIC=${TOPIC}"

exec java ${PROPS} -cp "/Home/app/OnapWebSocket/target/classes:/Home/app/OnapWebSocket/target/lib/*" "org.tcl.mvn.onap.WSMain"  > /dev/null 2>&1
