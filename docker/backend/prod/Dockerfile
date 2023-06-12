#
# Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
#
# File name: Dockerfile
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

# /backend/prod/Dockerfile

FROM amazoncorretto:17

ENV CACHEBUST=$CACHEBUST

ENV BUILD_DIR /build/jar
ENV ENTRY_DIR /jwizard-container

COPY $BUILD_DIR/gfx $ENTRY_DIR/gfx
COPY $BUILD_DIR/.env $ENTRY_DIR
COPY $BUILD_DIR/*.txt $ENTRY_DIR
COPY $BUILD_DIR/*.jar $ENTRY_DIR
COPY $BUILD_DIR/properties-prod.yml $ENTRY_DIR

WORKDIR $ENTRY_DIR
RUN mv *.jar jwizard-embeddable.jar

EXPOSE 8080
ENTRYPOINT [ "java", "-Dspring.profiles.active=docker", "-jar", "jwizard-embeddable.jar" ]