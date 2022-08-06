/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: BotActivityThreadingSequencer.java
 * Last modified: 01/08/2022, 20:57
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

package pl.miloszgilga.franekbotapp;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;

import java.util.*;

import pl.miloszgilga.franekbotapp.configuration.ActivityStatusSequencerConfiguration;
import static pl.miloszgilga.franekbotapp.configuration.ConfigurationLoader.config;


class BotActivitySequencer extends TimerTask {

    private static final ActivityStatusSequencerConfiguration seqConfig = config.getActivitySequencerConfiguration();
    private static volatile BotActivitySequencer sequencer;
    private final JDA jda;

    private final List<BotCommand> activityRandomizerElements = BotCommand.getAllCommandsAsEnumValues();
    private final Timer timer = new Timer();
    private int elementPos = 0;

    private BotActivitySequencer(JDA jda) {
        if (sequencer != null) throw new IllegalArgumentException();
        this.jda = jda;
    }

    void invokeSequencer() {
        if (sequencer == null || seqConfig == null) return;
        timer.schedule(sequencer, 0, seqConfig.getIntervalSeconds() * 1000L);
    }

    @Override
    public void run() {
        if (jda == null) return;
        if (!seqConfig.isEnableRandomizeActivityStatus()) jda.getPresence().setActivity(Activity.listening("OFF"));

        if (elementPos == activityRandomizerElements.size()) elementPos = 0;
        String selectedActivity = activityRandomizerElements.get(elementPos++).getCommandName();
        jda.getPresence().setActivity(Activity.listening(config.getPrefix() + selectedActivity));
    }

    static synchronized BotActivitySequencer getSingletonInstance(JDA jda) {
        if (sequencer == null) {
            sequencer = new BotActivitySequencer(jda);
        }
        return sequencer;
    }
}