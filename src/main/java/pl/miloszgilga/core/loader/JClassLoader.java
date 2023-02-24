/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: JClassLoader.java
 * Last modified: 23/02/2023, 18:40
 * Project name: jwizard-discord-bot
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

package pl.miloszgilga.core.loader;

import lombok.extern.slf4j.Slf4j;

import org.reflections.Reflections;
import com.jagrosh.jdautilities.command.*;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.stereotype.Component;

import java.util.*;

import pl.miloszgilga.core.*;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.Bootloader.APP_CONTEXT;
import static pl.miloszgilga.core.configuration.BotProperty.J_PREFIX;

import static org.reflections.util.ClasspathHelper.forPackage;
import static org.reflections.scanners.Scanners.TypesAnnotated;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@Component
public class JClassLoader {

    final org.reflections.Configuration commandsReflectionConfig = new ConfigurationBuilder()
        .setUrls(forPackage("pl.miloszgilga.command")).setScanners(TypesAnnotated);
    private final Reflections commandsReflections = new Reflections(commandsReflectionConfig);

    final org.reflections.Configuration listenersReflectionsConfig = new ConfigurationBuilder()
        .setUrls(forPackage("pl.miloszgilga.listener")).setScanners(TypesAnnotated);
    private final Reflections listenersReflections = new Reflections(listenersReflectionsConfig);

    private final Set<JDACommand> loadedCommands = new HashSet<>();
    private final Set<JDAListenerAdapter> loadedListeners = new HashSet<>();

    private final BotConfiguration config;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public JClassLoader(BotConfiguration config) {
        this.config = config;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void loadCommandsViaReflection() {
        final Set<Class<?>> commandsClazz = commandsReflections.getTypesAnnotatedWith(JDACommandLazyService.class);
        if (commandsClazz.isEmpty()) return;
        for (final Class<?> commandClazz : commandsClazz) {
            loadedCommands.add((JDACommand) APP_CONTEXT.getBean(commandClazz));
        }
        log.info("Successfully loaded command interceptors ({}):", loadedCommands.size());
        for (final JDACommand jdaCommand : loadedCommands) {
            final String commandInvoker = config.getProperty(J_PREFIX) + jdaCommand.getName();
            log.info(" --- {} {} ({})", commandInvoker, jdaCommand.getAliases(), jdaCommand.getClass().getName());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void loadListenersViaReflection() {
        final Set<Class<?>> listenersClazz = listenersReflections.getTypesAnnotatedWith(JDAListenerLazyService.class);
        if (listenersClazz.isEmpty()) return;
        for (final Class<?> listenerClazz : listenersClazz) {
            loadedListeners.add((JDAListenerAdapter) APP_CONTEXT.getBean(listenerClazz));
        }
        log.info("Successfully loaded listener adapter interceptors ({}):", loadedListeners.size());
        for (final Object jdaListener : loadedListeners) {
            log.info(" --- {} ({})", jdaListener.getClass().getSimpleName(), jdaListener.getClass().getName());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Command[] getLoadedCommands() {
        return loadedCommands.toArray(Command[]::new);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Object[] getAllListeners(CommandClient commandClient) {
        final Object[] objects = new Object[loadedListeners.size() + 1];
        objects[0] = commandClient;
        System.arraycopy(loadedListeners.toArray(), 0, objects, 1, loadedListeners.size());
        return objects;
    }
}
