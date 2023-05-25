/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: DebugLocaleSet.java
 * Last modified: 04/04/2023, 18:51
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

package pl.miloszgilga.locale;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import pl.miloszgilga.core.IEnumerableLocaleSet;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum DebugLocaleSet implements IEnumerableLocaleSet {

    GENERAL_HEADER_DEBUG                            ("jwizard.message.debug.header.General"),
    CONFIGURATION_HEADER_DEBUG                      ("jwizard.message.debug.header.Configuration"),
    VERSIONS_HEADER_DEBUG                           ("jwizard.message.debug.header.Versions"),
    JVM_HEADER_DEBUG                                ("jwizard.message.debug.header.JavaVirtualMachine"),

    JVM_NAME_JAVA_DEBUG                             ("jwizard.message.debug.java.JVMName"),
    JVM_VERSION_JAVA_DEBUG                          ("jwizard.message.debug.java.JVMVersion"),
    JVM_SPEC_VERSION_JAVA_DEBUG                     ("jwizard.message.debug.java.JVMSpecVersion"),
    JRE_NAME_JAVA_DEBUG                             ("jwizard.message.debug.java.JREName"),
    JRE_VERSION_JAVA_DEBUG                          ("jwizard.message.debug.java.JREVersion"),
    JRE_SPEC_VERSION_JAVA_DEBUG                     ("jwizard.message.debug.java.JRESpecVersion"),
    OS_NAME_JAVA_DEBUG                              ("jwizard.message.debug.java.OSName"),
    OS_ARCHITECTURE_JAVA_DEBUG                      ("jwizard.message.debug.java.OSArchitecture"),

    BOT_VERSION_DEBUG                               ("jwizard.message.debug.BotVersion"),
    BOT_LOCALE_DEBUG                                ("jwizard.message.debug.BotLocale"),
    CURRENT_GUILD_OWNER_TAG_DEBUG                   ("jwizard.message.debug.CurrentGuildOwnerTag"),
    CURRENT_GUILD_ID_DEBUG                          ("jwizard.message.debug.CurrentGuildId"),
    DEFAULT_PREFIX_DEBUG                            ("jwizard.message.debug.DefaultPrefix"),
    ENABLE_SLASH_COMMANDS_DEBUG                     ("jwizard.message.debug.EnableSlashCommands"),
    VOTE_MAX_WAITING_TIME_DEBUG                     ("jwizard.message.debug.VoteMaxWaitingTime"),
    LEAVE_CHANNEL_WAITING_TIME_DEBUG                ("jwizard.message.debug.LeaveChannelWaitingTime"),
    JDA_VERSION_DEBUG                               ("jwizard.message.debug.JdaVersion"),
    JDA_UTILITIES_VERSION_DEBUG                     ("jwizard.message.debug.JdaUtilitiesVersion"),
    LAVAPLAYER_VERSION_DEBUG                        ("jwizard.message.debug.LavaplayerVersion"),
    JVM_XMX_MEMORY_DEBUG                            ("jwizard.message.debug.JVMXmxMemory"),
    JVM_USED_MEMORY_DEBUG                           ("jwizard.message.debug.JVMUsedMemory");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String holder;
}
