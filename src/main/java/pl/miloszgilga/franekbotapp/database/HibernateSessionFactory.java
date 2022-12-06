/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: HibernateSessionFactory.java
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

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import org.slf4j.Logger;
import org.hibernate.Session;
import org.slf4j.LoggerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

import java.sql.SQLException;

import pl.miloszgilga.franekbotapp.exceptions.UnableToLoadDatabaseConfigurationException;


public class HibernateSessionFactory {

    private static final Logger logger = LoggerFactory.getLogger(HibernateInitializer.class);
    private static final String LIQUIBASE_CHANGELOG_XML = "classpath:database/database.changelog.xml";
    private static volatile HibernateSessionFactory instance;

    private final HibernateInitializer initializer = HibernateInitializer.getSingletonInstance();
    private SessionFactory sessionFactory;

    private HibernateSessionFactory() {
        initialiseHibernateSessionFactory();
    }

    private void initialiseHibernateSessionFactory() {
        try {
            initializer.initialiseProperties();

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(initializer.getConfiguration().getProperties()).build();

            MetadataSources metadataSources = new MetadataSources(serviceRegistry);
            ConnectionProvider provider = metadataSources.getServiceRegistry().getService(ConnectionProvider.class);
            JdbcConnection jdbcConnection = new JdbcConnection(provider.getConnection());

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);
            Liquibase liquibase = new Liquibase(LIQUIBASE_CHANGELOG_XML, new ClassLoaderResourceAccessor(), database);
            liquibase.update("testificatemigration");

            sessionFactory = initializer.getConfiguration().buildSessionFactory(serviceRegistry);

        } catch (SQLException | DatabaseException ex) {
            logger.error("Nieudane połączenie z bazą danych");
        } catch (LiquibaseException ex) {
            logger.error("Nieudana testowa migracja usługi liquibase");
        } catch (UnableToLoadDatabaseConfigurationException ex) {
            logger.error("Nieudane załadowanie ustawień dostępu do bazy danych dla '{}'", ex.getMessage());
        }
    }

    public Session openTransactionalSessionAndBeginTransaction() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        return session;
    }

    public static synchronized HibernateSessionFactory getSingletonInstance() {
        if (instance == null) {
            instance = new HibernateSessionFactory();
        }
        return instance;
    }
}
