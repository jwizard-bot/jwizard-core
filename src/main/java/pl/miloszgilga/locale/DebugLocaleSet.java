/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CommandLocale.java
 * Last modified: 04/04/2023, 18:15
 * Project name: jwizard-discord-bot
 *
 * Licensed under the MIT license; you may not use this file except in compliance with the License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL COPIES OR
 * SUBSTANTIAL PORTIONS OF THE SOFTWARE.
 *
 * The software is provided "as is", without warranty of any kind, express or implied, including but not limited
 * to the warranties of merchantability, fitness for a particular purpose and noninfringement. In no event
 * shall the authors or copyright holders be liable for any claim, damages or other liability, whether in an
 * action of contract, tort or otherwise, arising from, out of or in connection with the software or the use
 * or other dealings in the software.
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
