/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: LoggerOutputConsolePrinter.java
 * Last modified: 16/07/2022, 19:59
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

import net.dv8tion.jda.api.entities.Guild;

import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;


public class LoggerOutputConsolePrinter implements ILoggerOutputPrinter {

    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\033[0;32m";
    private static final String ANSI_YELLOW = "\033[0;33m";
    private static final String ANSI_RED = "\033[0;31m";
    private static final String ANSI_CYAN = "\033[0;36m";

    @Override
    public void loggerOutputPrinter(String message, LoggerRank rank, Guild guild, Class<?> authorClazz) {
        Date date = new Date();
        System.out.print("[" + formatter.format(date) + "]\t");
        System.out.print(chooseColor(rank) + "[" + rank.getRank().toUpperCase(Locale.ROOT) + "]" + ANSI_RESET + "\t");
        System.out.print(ANSI_CYAN + "[" + authorClazz.getSimpleName() + "]\t" + ANSI_RESET);
        System.out.print("[Serwer: " + guild.getName() + "]\t");
        System.out.print(" : " + message + "\n");
    }

    private String chooseColor(LoggerRank rank) {
        switch (rank) {
            case INFO: return ANSI_GREEN;
            case WARN: return ANSI_YELLOW;
            case ERROR: return ANSI_RED;
        }
        return ANSI_RESET;
    }
}