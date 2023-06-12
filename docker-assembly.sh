#!/bin/bash

#
# Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
#
# File name: docker-assembly.sh
# Last modified: 6/12/23, 5:08 AM
# Project name: jwizard-discord-bot
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
# file except in compliance with the License. You may obtain a copy of the License at
#
#     <http://www.apache.org/license/LICENSE-2.0>
#
# Unless required by applicable law or agreed to in writing, software distributed under
# the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific language
# governing permissions and limitations under the license.
#

ARG_KEY="--mode"
IS_ARG_NOT_EXIST=false

if [ "$#" -eq 0 ]; then
    IS_ARG_NOT_EXIST=true
fi

IFS="=" read -r key value <<< "$@"
if [ "$key" == "$ARG_KEY" ]; then
    if [[ "$value" != "dev" && "$value" != "prod" ]]; then
        echo "[bash docker script err] <> Available only argument: --mode=<dev|prod>"
        exit 1
    fi
    MODE=$value
else
    IS_ARG_NOT_EXIST=true
fi

if [ $IS_ARG_NOT_EXIST == true ]; then
    echo "[bash docker script err] <> Available only argument: --mode=<dev|prod>"
    exit 2
fi

BUILD_DATE=$(date +%Y%m%d%H%M%S)
export BUILD_DATE

echo "[bash docker script info] <> Preparing bootable JAR directory..."
./gradlew clean --warning-mode none || return 3

echo "[bash docker script info] <> Creating bootable JAR directory..."
./gradlew bootJar --warning-mode none || return 4

echo "[bash docker script info] <> Running Docker cluster in '$MODE' mode..."
docker-compose -f "docker-compose-$MODE.yml" up || return 5
