/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: JClassLoader.java
 * Last modified: 07/04/2023, 16:24
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
