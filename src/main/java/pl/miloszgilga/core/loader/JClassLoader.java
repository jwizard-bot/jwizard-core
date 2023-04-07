/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: JClassLoader.java
 * Last modified: 19/03/2023, 23:17
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

package pl.miloszgilga.core.loader;

import lombok.extern.slf4j.Slf4j;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.CommandClient;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.core.AbstractCommand;
import pl.miloszgilga.core.AbstractListenerAdapter;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@Component
public class JClassLoader {

    private final org.reflections.Configuration reflectionConfig = new ConfigurationBuilder()
        .setUrls(ClasspathHelper.forJavaClassPath()).setScanners(Scanners.TypesAnnotated);

    private final Reflections commandsReflections = new Reflections(reflectionConfig);
    private final Reflections listenersReflections = new Reflections(reflectionConfig);

    private final List<AbstractCommand> loadedCommands = new ArrayList<>();
    private final List<AbstractListenerAdapter> loadedListeners = new ArrayList<>();

    private final BotConfiguration config;
    private final ApplicationContext context;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public JClassLoader(BotConfiguration config, ApplicationContext context) {
        this.config = config;
        this.context = context;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void loadCommandsViaReflection() {
        final Set<Class<?>> commandsClazz = commandsReflections
            .getTypesAnnotatedWith(JDAInjectableCommandLazyService.class);
        if (commandsClazz.isEmpty()) return;
        for (final Class<?> commandClazz : commandsClazz) {
            loadedCommands.add((AbstractCommand) context.getBean(commandClazz));
        }
        log.info("Successfully loaded command interceptors ({}):", loadedCommands.size());
        loadedCommands.sort(Comparator.comparing(Command::getName));
        for (final AbstractCommand abstractCommand : loadedCommands) {
            final String commandInvoker = config.getProperty(BotProperty.J_PREFIX) + abstractCommand.getName();
            log.info(" --- {} {} ({}), slash command exist: {}", commandInvoker, abstractCommand.getAliases(),
                abstractCommand.getClass().getName(),
                BotCommand.checkIfSlashExist(abstractCommand.getName()) ? "YES" : "NO");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void loadListenersViaReflection() {
        final Set<Class<?>> listenersClazz = listenersReflections
            .getTypesAnnotatedWith(JDAInjectableListenerLazyService.class);
        if (listenersClazz.isEmpty()) return;
        for (final Class<?> listenerClazz : listenersClazz) {
            loadedListeners.add((AbstractListenerAdapter) context.getBean(listenerClazz));
        }
        log.info("Successfully loaded listener adapter interceptors ({}):", loadedListeners.size());
        for (final Object jdaListener : loadedListeners) {
            log.info(" --- {} ({})", jdaListener.getClass().getSimpleName(), jdaListener.getClass().getName());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public SlashCommand[] getLoadedCommands() {
        return loadedCommands.toArray(SlashCommand[]::new);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Object[] getAllListeners(CommandClient commandClient) {
        final Object[] objects = new Object[loadedListeners.size() + 2];
        objects[0] = commandClient;
        objects[1] = config.getEventWaiter();
        for (int i = 2; i < loadedListeners.size() + 2; i++) {
            objects[i] = loadedListeners.get(i - 2);
        }
        return objects;
    }
}
