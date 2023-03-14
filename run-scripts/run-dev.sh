#!/bin/bash

#
# Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
#
# File name: run-dev.sh
# Last modified: 28/02/2023, 19:35
# Project name: jwizard-discord-bot
#
# Licensed under the MIT license; you may not use this file except in compliance with the License.
#
# Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
# documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
# rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
# permit persons to whom the Software is furnished to do so, subject to the following conditions:
#
# THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL
# COPIES OR SUBSTANTIAL PORTIONS OF THE SOFTWARE.
#

START_JAVA_HEAP_SIZE="256m"     # -Xms parameter, min. 128MB
MAX_JAVA_HEAP_SIZE="512m"       # -Xmx parameter

EXEC_JAR_FILE_NAME="jwizard-discord-bot-[0-9]\.[0-9]\.[0-9]\.jar"

def_executable_exist() {
    EXEC_JAR_FILE_NAME=$(find . -name "$EXEC_JAR_FILE_NAME" -exec  echo {} \;)
    if [ "$EXEC_JAR_FILE_NAME" == "" ]; then
        echo "[bash run script err] <> Executable JAR file not found in current directory"
        exit 1
    fi
}

if [ "$#" -gt 1 ]; then
    echo "[bash run script err] <> Available only argument: --execJar=<nameOfJarFile>"
    exit 1
fi

if [ "$#" -eq 1 ]; then
    IFS="=" read -r key value <<< "$@"
    if [ "$key" == "--execJar" ]; then
        EXEC_JAR_FILE_NAME=$value
    else
        def_executable_exist
    fi
else
    def_executable_exist
fi

if type -p java; then
    JAVA=java
elif [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA="$JAVA_HOME/bin/java"
else
    echo "[bash run script err] <> Java Virtual Machine (JVM) not found"
    exit 2
fi

JAVA_VERSION=$($JAVA -version 2>&1 | awk -F '"' '/version/ {print $2}')
JAVA_VERSION=$(echo "$JAVA_VERSION" | cut -d "." -f1)

if [ "$JAVA_VERSION" -ne 17 ]; then
    echo "[bash run script err] <> To run application you must have installed JRE 17.X"
    exit 3
fi

if [ ! -f "properties-dev.yml" ]; then
    echo "[bash run script err] <> Configuration file properties-dev.yml not found in current directory"
    echo "[bash run script err] <> Download file from:"
    echo "[bash run script err] <> https://github.com/Milosz08/JWizard_Discord_Bot/blob/master/properties-dev.yml"
    exit 5
fi

if [ ! -f ".env" ]; then
    echo "[bash run script err] <> Env file not found in current directory"
    exit 6
fi

EXEC_SCRIPT="java
-Xmx$MAX_JAVA_HEAP_SIZE -Xms$START_JAVA_HEAP_SIZE
-Duser.timezone=UTC
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/
-jar $EXEC_JAR_FILE_NAME
--mode=dev
"

EXEC_SCRIPT=$(echo "$EXEC_SCRIPT" | tr '\n' ' ')

echo "[bash run script info] <> Executing JWizard bot JAR file in development mode..."
echo "[bash run script info] <> $EXEC_SCRIPT"

$EXEC_SCRIPT