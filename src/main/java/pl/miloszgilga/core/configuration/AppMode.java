/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AppMode.java
 * Last modified: 08/04/2023, 20:56
 * Project name: jwizard-discord-bot
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.miloszgilga.core.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.core.env.Environment;

import java.util.Arrays;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum AppMode {
    PROD    ("PROD",    "properties-prod.yml",  "production",   "prod"),
    DEV     ("DEV",     "properties-dev.yml",   "development",  "dev"),
    DOCKER  ("PROD",    "properties-prod.yml",  "docker",       "docker");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String mode;
    private final String configFile;
    private final String alias;
    private final String springAlias;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static AppMode findModeBaseSpringProfile(String profile) {
        return Arrays.stream(values())
            .filter(p -> p.springAlias.equals(profile))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("App only support one spring profile: 'dev', 'prod' or 'docker'."));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean isDevProfileActive(Environment environment) {
        return environment.getActiveProfiles()[0].equals(DEV.springAlias);
    }
}
