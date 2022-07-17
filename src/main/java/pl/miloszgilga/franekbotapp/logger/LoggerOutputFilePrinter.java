/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: LoggerOutputFilePrinter.java
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

import java.io.File;
import java.util.Date;
import java.util.Locale;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;


public class LoggerOutputFilePrinter implements ILoggerOutputPrinter {

    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private final SimpleDateFormat fileNameDateFormatter = new SimpleDateFormat("ddMMyyyy");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMM");

    @Override
    public void loggerOutputPrinter(String message, LoggerRank rank, Guild guild, Class<?> authorClazz) {
        Date date = new Date();
        PrintWriter out;
        try {
            String logsDirPath = LoggerFactory.getLogsFolderPath();
            String serverGuidDirName = guild.getId() + "_" + guild.getName();
            String logDataDirName = generateDirNameBaseDate(guild);

            File serverDir = new File(logsDirPath + serverGuidDirName);
            if (!serverDir.exists()) {
                if (!serverDir.mkdir()) throw new IOException();
            }

            File dateLogDir = new File(logsDirPath + serverGuidDirName + LoggerFactory.getFileSeparator() + logDataDirName);
            if (!dateLogDir.exists()) {
                if (!dateLogDir.mkdir()) throw new IOException();
            }

            String fullLogFilePath = logsDirPath + serverGuidDirName + LoggerFactory.getFileSeparator() +
                    logDataDirName + LoggerFactory.getFileSeparator() + generateFileNameBaseDate(guild);
            File logFile = new File(fullLogFilePath);
            if (logFile.exists() && !logFile.isDirectory() ) {
                out = new PrintWriter(new FileOutputStream(fullLogFilePath, true));
            } else {
                out = new PrintWriter(fullLogFilePath);
            }

            out.append("[").append(formatter.format(date)).append("]\t");
            out.append("[").append(rank.getRank().toUpperCase(Locale.ROOT)).append("]\t");
            out.append("[").append(authorClazz.getSimpleName()).append("]\t");
            out.append("[Serwer: ").append(guild.getName()).append("]\t : ").append(message).append("\n");
            out.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private String generateDirNameBaseDate(Guild guild) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today;
        while (monday.getDayOfWeek() != DayOfWeek.MONDAY) {
            monday = monday.minusDays(1);
        }
        LocalDate sunday = today;
        while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
            sunday = sunday.plusDays(1);
        }
        return String.format("%s%s%s__srv%s",
                monday.format(dateFormatter), sunday.format(dateFormatter), today.getYear(), guild.getId());
    }

    private String generateFileNameBaseDate(Guild guild) {
        return String.format("%s__srv%s__logfile.log", fileNameDateFormatter.format(new Date()), guild.getId());
    }
}