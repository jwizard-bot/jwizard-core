/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: LoggerConfigurationLoader.java
 * Last modified: 17/07/2022, 12:22
 * Project name: franek-bot
 *
 * Licensed under the MIT license; you may not use this file except in compliance with the License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL
 * COPIES OR SUBSTANTIAL PORTIONS OF THE SOFTWARE.
 */

package pl.miloszgilga.franekbotapp.logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.FileNotFoundException;

import pl.miloszgilga.franekbotapp.FranekBot;


class LoggerConfigurationLoader {

    private static final InputStream FILE = FranekBot.class.getResourceAsStream("/config/logger-config.json");
    static LoggerConfigurationLoader loggerConfigurationLoader;
    private LoggerConfiguration loggerConfig;

    private LoggerConfigurationLoader() {
        loadConfiguration();
    }

    private void loadConfiguration() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (FILE == null) {
                throw new FileNotFoundException("Plik konfiguracyjny loggera nie istnieje!");
            }
            loggerConfig = objectMapper.readValue(new String(FILE.readAllBytes()), LoggerConfiguration.class);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    static LoggerConfigurationLoader getSingleton() {
        if (loggerConfigurationLoader == null) {
            loggerConfigurationLoader = new LoggerConfigurationLoader();
        }
        return loggerConfigurationLoader;
    }

    LoggerConfiguration getLoggerConfig() {
        return loggerConfig;
    }
}