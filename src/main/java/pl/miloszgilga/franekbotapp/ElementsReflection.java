/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ElementsReflection.java
 * Last modified: 27/07/2022, 18:33
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

import org.reflections.Reflections;
import com.jagrosh.jdautilities.command.Command;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pl.miloszgilga.franekbotapp.logger.LoggerFactory;

import java.util.Set;
import java.util.Deque;
import java.util.HashSet;


class ElementsReflection {

    private static final String EXECUTORS_PACKAGE = "pl.miloszgilga.franekbotapp.executors";
    private static final String INTERCEPTORS_PACKAGE = "pl.miloszgilga.franekbotapp.interceptors";
    private static volatile ElementsReflection instance;

    Command[] reflectAllCommandExecutors(final LoggerFactory logger) {
        final Reflections reflections = new Reflections(EXECUTORS_PACKAGE);
        final Set<Class<? extends Command>> executorsClazz = reflections.getSubTypesOf(Command.class);
        final Set<Command> executors = new HashSet<>();

        executorsClazz.forEach(clazz -> {
            try {
                executors.add(clazz.getDeclaredConstructor().newInstance());
                logger.debug(String.format("Egzekutor '%s' załadowany pomyślnie poprzez mechanizm refleksji",
                        clazz.getSimpleName()), null);
            } catch (Exception ignored) {
                logger.error(String.format("Wystąpił problem z załadowaniem egzekutora '%s' poprzez mechanizm refleksji",
                        clazz.getSimpleName()), null);
            }
        });

        return executors.toArray(Command[]::new);
    }

    Object[] reflectAllInterceptors(final LoggerFactory logger, final Deque<Object> interceptors) {
        final Reflections reflections = new Reflections(INTERCEPTORS_PACKAGE);
        final Set<Class<? extends ListenerAdapter>> interceptorsClazz = reflections.getSubTypesOf(ListenerAdapter.class);

        interceptorsClazz.forEach(clazz -> {
            try {
                interceptors.add(clazz.getDeclaredConstructor().newInstance());
                logger.debug(String.format("Interceptor '%s' załadowany pomyślnie poprzez mechanizm refleksji",
                        clazz.getSimpleName()), null);
            } catch (Exception ignored) {
                logger.error(String.format("Wystąpił problem z załadowaniem interceptora '%s' poprzez mechanizm refleksji",
                        clazz.getSimpleName()), null);
            }
        });

        return interceptors.toArray();
    }

    static synchronized ElementsReflection getSingletonInstance() {
        if (instance == null) {
            instance = new ElementsReflection();
        }
        return instance;
    }
}