/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SystemProperty.java
 * Last modified: 16/05/2023, 18:48
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

package pl.miloszgilga.system;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Arrays;
import java.util.function.BiFunction;

import net.dv8tion.jda.api.entities.Guild;
import pl.miloszgilga.locale.DebugLocaleSet;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum SystemProperty {
    JVM_NAME                        ("java.vm.name",                    DebugLocaleSet.JVM_NAME_JAVA_DEBUG),
    JVM_VERSION                     ("java.version",                    DebugLocaleSet.JVM_VERSION_JAVA_DEBUG),
    JVM_SPEC_VERSION                ("java.vm.specification.version",   DebugLocaleSet.JVM_SPEC_VERSION_JAVA_DEBUG),

    JRE_NAME                        ("java.runtime.name",               DebugLocaleSet.JRE_NAME_JAVA_DEBUG),
    JRE_VERSION                     ("java.runtime.version",            DebugLocaleSet.JRE_VERSION_JAVA_DEBUG),
    JRE_SPEC_VERSION                ("java.specification.version",      DebugLocaleSet.JRE_SPEC_VERSION_JAVA_DEBUG),

    OS_NAME                         ("os.name",                         DebugLocaleSet.OS_NAME_JAVA_DEBUG),
    OS_ARCHITECTURE                 ("os.arch",                         DebugLocaleSet.OS_ARCHITECTURE_JAVA_DEBUG);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String property;
    private final DebugLocaleSet localeSet;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static List<String> getAllFormatted(BotConfiguration config, BiFunction<String, String, String> formatter, Guild guild) {
        return Arrays.stream(values())
            .map(v -> formatter.apply(config.getLocaleText(v.localeSet, guild), System.getProperty(v.property)))
            .toList();
    }
}
