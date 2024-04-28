#!/bin/bash

if [[ ! -f ${JWT_ISSUER_URI_FILE} ]]; then
    echo "The file ${JWT_ISSUER_URI_FILE} does not exist"
    exit 1
fi

export JWT_ISSUER_URI=`cat ${JWT_ISSUER_URI_FILE}`

java org.springframework.boot.loader.launch.JarLauncher