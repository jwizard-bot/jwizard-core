/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ChannelLoggerReflectAuditableInterceptors.java
 * Last modified: 08/08/2022, 22:24
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

package pl.miloszgilga.franekbotapp.channellogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.reflections.Reflections;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import pl.miloszgilga.franekbotapp.configuration.ChannelLoggerConfiguration;
import static pl.miloszgilga.franekbotapp.configuration.ConfigurationLoader.config;


public class AuditableInterceptorsReflection {

    private static final String AUDITABLE_INTERCEPTORS_PACKAGE =
            "pl.miloszgilga.franekbotapp.channellogger.auditableinterceptors";

    private static final Logger logger = LoggerFactory.getLogger(AuditableInterceptorsReflection.class);
    private static final ChannelLoggerConfiguration loggerConfig = config.getChannelLoggerConfiguration();
    private static volatile AuditableInterceptorsReflection instance;

    private AuditableInterceptorsReflection() {
        if (instance != null) throw new IllegalArgumentException();
    }

    public Object[] reflectAllAuditableInterceptors() {
        if (!loggerConfig.isLoggerEnabled()) return new Object[] {};

        final List<IBasicAuditableInterceptor> auditableInterceptors = new ArrayList<>();
        final Reflections reflections = new Reflections(AUDITABLE_INTERCEPTORS_PACKAGE);
        final Set<Class<? extends IBasicAuditableInterceptor>> auditableInterceptorsClazz =
                reflections.getSubTypesOf(IBasicAuditableInterceptor.class);

        for(Class<? extends IBasicAuditableInterceptor> clazz : auditableInterceptorsClazz) {
            try {
                auditableInterceptors.add(clazz.getDeclaredConstructor().newInstance());
                logger.info("Interceptor audytowy '{}' załadowany pomyślnie poprzez mechanizm refleksji",
                        clazz.getSimpleName());
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error("Wystąpił problem z załadowaniem interceptora audytowego '{}' poprzez mechanizm refleksji",
                        clazz.getSimpleName());
            }
        }

        return auditableInterceptors.toArray();
    }

    public static synchronized AuditableInterceptorsReflection getSingletonInstance() {
        if (instance == null) {
            instance = new AuditableInterceptorsReflection();
        }
        return instance;
    }
}