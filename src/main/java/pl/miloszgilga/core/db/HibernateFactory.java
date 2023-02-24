/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: HibernateInitializer.java
 * Last modified: 24/02/2023, 02:58
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

package pl.miloszgilga.core.db;

import lombok.extern.slf4j.Slf4j;

import liquibase.Liquibase;
import liquibase.database.*;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.boot.MetadataSources;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

import org.reflections.Reflections;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Component;
import org.reflections.util.ConfigurationBuilder;

import java.util.*;
import java.sql.SQLException;

import pl.miloszgilga.core.configuration.BotConfiguration;

import static java.util.Objects.isNull;
import static org.hibernate.cfg.AvailableSettings.*;
import static org.reflections.util.ClasspathHelper.forPackage;
import static org.reflections.scanners.Scanners.TypesAnnotated;

import static pl.miloszgilga.core.configuration.BotProperty.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@Component
public class HibernateFactory {

    private final BotConfiguration config;
    private SessionFactory sessionFactory;

    private final Properties properties = new Properties();
    private final Configuration configuration = new Configuration();

    private static final String C3P0_CFG = "db/c3p0.cfg.xml";
    private static final String LIQUIBASE_CFG = "db/changelog/db.changelog.xml";

    private static final String LIQUIBASE_CHANGELOG_TABLE = "_liquibase_changelog";
    private static final String LIQUIBASE_CHANGELOG_LOCK_TABLE = "_liquibase_changelog_lock";

    final org.reflections.Configuration reflectionConfig = new ConfigurationBuilder()
        .setUrls(forPackage("pl.miloszgilga.entities")).setScanners(TypesAnnotated);
    private final Reflections reflections = new Reflections(reflectionConfig);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public HibernateFactory(BotConfiguration config) {
        this.config = config;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void loadConfiguration() {
        final URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.addParameter("createDatabaseIfNotExist", config.getProperty(J_DB_CREATE));
        uriBuilder.addParameter("useSSL", config.getProperty(J_DB_ENFORCE_SSL));

        properties.setProperty(URL, config.getProperty(J_DB_CONNECTION) + uriBuilder);
        properties.setProperty(USER, config.getProperty(J_DB_USERNAME));
        properties.setProperty(PASS, config.getProperty(J_DB_PASSWORD));
        properties.setProperty(DRIVER, config.getProperty(J_HDB_DRIVER));
        properties.setProperty(DIALECT, config.getProperty(J_HDB_DIALECT));
        properties.setProperty(SHOW_SQL, config.getProperty(J_HDB_SQL_OUT));
        properties.setProperty(HBM2DDL_AUTO, config.getProperty(J_HDB_HBM2DDL));

        final Set<Class<?>> hibernateEntitiesClazz = reflections.getTypesAnnotatedWith(ScannedHibernateEntity.class);
        for (final Class<?> hibernateEntitityClazz : hibernateEntitiesClazz) {
            log.debug("Successfully loaded hibernate entity: {}", hibernateEntitityClazz.getName());
            configuration.addAnnotatedClass(hibernateEntitityClazz);
        }
        configuration.setProperties(properties).configure(C3P0_CFG);
        log.info("Successfully loaded Hibernate configuration.");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void initialize() {
        if (!isNull(sessionFactory)) return;
        try {
            final ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(properties)
                .enableAutoClose()
                .build();

            final MetadataSources sources = new MetadataSources(serviceRegistry);
            final ConnectionProvider provider = sources.getServiceRegistry().getService(ConnectionProvider.class);
            final JdbcConnection jdbcConnection = new JdbcConnection(provider.getConnection());

            final Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);
            final Liquibase liquibase = new Liquibase(LIQUIBASE_CFG, new ClassLoaderResourceAccessor(), database);

            liquibase.getDatabase().setDatabaseChangeLogTableName(LIQUIBASE_CHANGELOG_TABLE);
            liquibase.getDatabase().setDatabaseChangeLogLockTableName(LIQUIBASE_CHANGELOG_LOCK_TABLE);
            liquibase.update();

            sessionFactory = configuration.buildSessionFactory();
            log.info("Successfully created Hibernate session factory instance.");
        } catch (LiquibaseException | SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
