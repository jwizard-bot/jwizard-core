#!/bin/bash

#
# Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
#
# File name: entrypoint.sh
# Last modified: 6/13/23, 12:58 AM
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

java \
    -XX:+UseSerialGC \
    -Xss512k \
    -XX:MaxRAM="$XMX" \
    -Xms"$XMS" \
    -Xmx"$XMX" \
    -Dspring.profiles.active="$SPRING_PROFILES_ACTIVE" \
    -XX:NativeMemoryTracking=summary \
    -jar \
    jwizard-embeddable.jar
