/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ActivityStatusSequencer.java
 * Last modified: 14/03/2023, 15:41
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

package pl.miloszgilga.misc;

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.AbstractConfigLoadableComponent;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@Component
public class ActivityStatusSequencer extends AbstractConfigLoadableComponent {

    private final BotConfiguration config;

    private int position = 0;
    private JDA jda;

    private final List<BotCommand> botCommands = Arrays.stream(BotCommand.values()).toList();
    private final Deque<String> cachedTextActivities = new LinkedList<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ActivityStatusSequencer(BotConfiguration config) {
        this.config = config;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void abstractLoadConfiguration(Object... params) {
        if (!config.getProperty(BotProperty.J_RR_ACTIVITY_ENABLED, Boolean.class)) return;
        this.jda = (JDA) params[0];

        if (config.getProperty(BotProperty.J_RR_EXTERNAL_FILE_ENABLED, Boolean.class)) {
            try {
                final InputStream fileStream = new FileInputStream(config.getProperty(BotProperty.J_RR_EXTERNAL_FILE_PATH));
                try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileStream))) {
                    String line;
                    while (!Objects.isNull(line = bufferedReader.readLine())) {
                        cachedTextActivities.add(line);
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (!config.getProperty(BotProperty.J_RR_COMMANDS_ENABLED, Boolean.class)) {
            botCommands.stream()
                .map(c -> config.getProperty(BotProperty.J_PREFIX) + c.getName())
                .forEach(cachedTextActivities::addLast);
        }
        if (config.getProperty(BotProperty.J_RR_RANDOMIZED, Boolean.class)) {
            Collections.shuffle((List<?>) cachedTextActivities);
        }
        log.info("Successfully loaded activity sequencer with parameters");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void abstractInitializeComponent() {
        if (!config.getProperty(BotProperty.J_RR_ACTIVITY_ENABLED, Boolean.class)) return;

        final int interval = config.getProperty(BotProperty.J_RR_INTERVAL, Integer.class);
        log.info("Invoke activity sequencer thread. Inverval: {} sec per activity", interval);

        config.getThreadPool().scheduleWithFixedDelay(() -> {
            if (Objects.isNull(jda)) return;
            final String selectedActivity = ((LinkedList<String>) cachedTextActivities).get(position);
            jda.getPresence().setActivity(Activity.listening(selectedActivity));
            position = (position + 1) % cachedTextActivities.size();
        }, 0, interval, TimeUnit.SECONDS);
    }
}
