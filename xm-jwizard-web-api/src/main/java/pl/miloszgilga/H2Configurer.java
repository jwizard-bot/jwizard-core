/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: H2Configurer.java
 * Last modified: 07/04/2023, 13:07
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

package pl.miloszgilga;

import lombok.extern.slf4j.Slf4j;

import org.h2.tools.Server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@Configuration
public class H2Configurer {

    @Profile("dev")
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server() throws SQLException {
        final Server tcpServer = Server.createTcpServer("-tcp");
        log.info("Successful created H2 embeded TCP/IP server.");
        return tcpServer;
    }
}
