/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: LoggerFactory.java
 * Last modified: 16/07/2022, 18:25
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

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

import net.dv8tion.jda.api.entities.Guild;

import pl.miloszgilga.franekbotapp.configuration.LoggerRank;
import pl.miloszgilga.franekbotapp.configuration.LoggerConfiguration;

import static pl.miloszgilga.franekbotapp.configuration.LoggerRank.*;
import static pl.miloszgilga.franekbotapp.configuration.ConfigurationLoader.config;


public final class LoggerFactory {

    private final LoggerOutputConsolePrinter loggerOutputConsolePrinter = new LoggerOutputConsolePrinter();
    private final LoggerOutputFilePrinter loggerOutputFilePrinter = new LoggerOutputFilePrinter();
    private static final LoggerConfiguration loggerConfig = config.getMiscellaneous().getLogger();

    Class<?> loggingAuthorClazz;

    public LoggerFactory(Class<?> loggingAuthorClazz) {
        this.loggingAuthorClazz = loggingAuthorClazz;
        createFolderInstance();
    }

    public void info(Guild guild, String message, Object... args) {
        loggerPrintableInvoker(INFO, guild, message, args);
    }

    public void warn(Guild guild, String message, Object... args) {
        loggerPrintableInvoker(WARN, guild, message, args);
    }

    public void error(Guild guild, String message, Object... args) {
        loggerPrintableInvoker(ERROR, guild, message, args);
    }

    private void loggerPrintableInvoker(LoggerRank loggerRank, Guild guild, String message, Object... args) {
        if (!loggerConfig.isLoggerEnabled() || !loggerConfig.getLoggerSensitivity().contains(loggerRank)) return;
        final String parsedMessage = String.format(message, args);
        if (loggerConfig.isEnableLoggedToStandardOutput()) {
            loggerOutputConsolePrinter.loggerOutputPrinter(parsedMessage, loggerRank, guild, loggingAuthorClazz);
        }
        if (loggerConfig.isEnableLoggedToFileOutput()) {
            loggerOutputFilePrinter.loggerOutputPrinter(parsedMessage, loggerRank, guild, loggingAuthorClazz);
        }
    }

    private void createFolderInstance() {
        try {
            if (!loggerConfig.isEnableLoggedToFileOutput()) return;
            if (Files.exists(Paths.get(getLogsFolderPath()))) return;
            if (!new File(getLogsFolderPath()).mkdir()) throw new IOException();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    static String getLogsFolderPath() {
        if (loggerConfig.getLoggerOutputFolderName().equals("")) {
            throw new IllegalArgumentException("Nazwa folderu wyjściowego loggera nie może być pusta");
        }
        URL url = LoggerFactory.class.getProtectionDomain().getCodeSource().getLocation();
        String jarPath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8);
        return new File(jarPath)
                .getParentFile()
                .getPath() + getFileSeparator() + loggerConfig.getLoggerOutputFolderName() + getFileSeparator();
    }

    static String getFileSeparator() {
        return System.getProperty("file.separator");
    }
}
