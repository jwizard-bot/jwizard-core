/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: HibernateInitializer.java
 * Last modified: 25/07/2022, 23:18
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

package pl.miloszgilga.franekbotapp.database;

import org.reflections.Reflections;
import org.hibernate.cfg.Configuration;

import java.util.Set;
import java.util.Properties;

import pl.miloszgilga.franekbotapp.configuration.DatabaseConfiguration;
import pl.miloszgilga.franekbotapp.exceptions.UnableToLoadDatabaseConfigurationException;

import static org.hibernate.cfg.AvailableSettings.*;
import static pl.miloszgilga.franekbotapp.configuration.ConfigurationLoader.config;


class HibernateInitializer {

    private static final String ENTITIES_PACKAGE_NAME = "pl.miloszgilga.franekbotapp.database.entities";
    private static final DatabaseConfiguration JSON_CONFIG = config.getDbConfig();
    private static volatile HibernateInitializer initializer;

    private final Properties properties = new Properties();
    private final Configuration configuration = new Configuration();

    private HibernateInitializer() { }

    public void initialiseProperties() {
        if (JSON_CONFIG == null) {
            String devOrProdInfo = config.isDevelopmentMode() ? "wersja deweloperska" : "wersja produkcyjna";
            throw new UnableToLoadDatabaseConfigurationException(devOrProdInfo);
        }
        final String ifNotExist = JSON_CONFIG.isCreateDatabaseIfNotExist() ? "?createDatabaseIfNotExist=true" : "";

        properties.setProperty(URL, JSON_CONFIG.getDatabaseUrl() + ifNotExist);
        properties.setProperty(USER, JSON_CONFIG.getUsername());
        properties.setProperty(PASS, JSON_CONFIG.getPassword());
        properties.setProperty(DRIVER, JSON_CONFIG.getHibernateDriverPackage());
        properties.setProperty(DIALECT, JSON_CONFIG.getHibernateDialectPackage());
        properties.setProperty(SHOW_SQL, String.valueOf(JSON_CONFIG.isSqlOnStandardOutput()));
        properties.setProperty(HBM2DDL_AUTO, JSON_CONFIG.getHbm2ddlAutoMode());
        properties.setProperty(PHYSICAL_NAMING_STRATEGY, CustomPhysicalNamingStrategy.class.getName());

        for(Class<? extends BasicHibernateEntity> entityClazz : reflectAllEntities()) {
            configuration.addAnnotatedClass(entityClazz);
        }
        configuration.setProperties(properties);
    }

    private Set<Class<? extends BasicHibernateEntity>> reflectAllEntities() {
        final Reflections entitiesReflections = new Reflections(ENTITIES_PACKAGE_NAME);
        return entitiesReflections.getSubTypesOf(BasicHibernateEntity.class);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public static synchronized HibernateInitializer getSingletonInstance() {
        if (initializer == null) {
            initializer = new HibernateInitializer();
        }
        return initializer;
    }
}