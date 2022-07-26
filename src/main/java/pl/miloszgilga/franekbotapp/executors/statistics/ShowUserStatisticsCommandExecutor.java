/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ShowUserStatisticsCommandExecutor.java
 * Last modified: 26/07/2022, 01:48
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

package pl.miloszgilga.franekbotapp.executors.statistics;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import static pl.miloszgilga.franekbotapp.BotCommand.MAIN_STATS;


public class ShowUserStatisticsCommandExecutor extends Command {

    public ShowUserStatisticsCommandExecutor() {
        name = MAIN_STATS.getCommandName();
        help = MAIN_STATS.getCommandDescription();
    }

    @Override
    protected void execute(CommandEvent event) {

    }
}