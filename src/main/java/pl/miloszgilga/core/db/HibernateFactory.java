/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: HibernateFactory.java
 * Last modified: 18/03/2023, 23:08
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

package pl.miloszgilga.core.db;

import lombok.extern.slf4j.Slf4j;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.boot.MetadataSources;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Consumer;
import java.sql.SQLException;

import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.AbstractConfigLoadableComponent;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@Component
public class HibernateFactory extends AbstractConfigLoadableComponent {

    private final BotConfiguration config;
    private SessionFactory sessionFactory;

    private final Properties properties = new Properties();
    private final Configuration configuration = new Configuration();

    private static final String C3P0_CFG = "db/c3p0.cfg.xml";
    private static final String LIQUIBASE_CFG = "db/changelog/db.changelog.xml";

    private static final String LIQUIBASE_CHANGELOG_TABLE = "_liquibase_changelog";
    private static final String LIQUIBASE_CHANGELOG_LOCK_TABLE = "_liquibase_changelog_lock";

    final org.reflections.Configuration reflectionConfig = new ConfigurationBuilder()
        .setUrls(ClasspathHelper.forPackage("pl.miloszgilga.entities")).setScanners(Scanners.TypesAnnotated);
    private final Reflections reflections = new Reflections(reflectionConfig);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public HibernateFactory(BotConfiguration config) {
        this.config = config;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void abstractLoadConfiguration(Object... params) {
        final URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.addParameter("createDatabaseIfNotExist", config.getProperty(BotProperty.J_DB_CREATE));
        uriBuilder.addParameter("useSSL", config.getProperty(BotProperty.J_DB_ENFORCE_SSL));

        properties.setProperty(AvailableSettings.URL, config.getProperty(BotProperty.J_DB_CONNECTION) + uriBuilder);
        properties.setProperty(AvailableSettings.USER, config.getProperty(BotProperty.J_DB_USERNAME));
        properties.setProperty(AvailableSettings.PASS, config.getProperty(BotProperty.J_DB_PASSWORD));
        properties.setProperty(AvailableSettings.DRIVER, config.getProperty(BotProperty.J_HDB_DRIVER));
        properties.setProperty(AvailableSettings.DIALECT, config.getProperty(BotProperty.J_HDB_DIALECT));
        properties.setProperty(AvailableSettings.SHOW_SQL, config.getProperty(BotProperty.J_HDB_SQL_OUT));
        properties.setProperty(AvailableSettings.HBM2DDL_AUTO, config.getProperty(BotProperty.J_HDB_HBM2DDL));

        final Set<Class<?>> hibernateEntitiesClazz = reflections.getTypesAnnotatedWith(ScannedHibernateEntity.class);
        for (final Class<?> hibernateEntitityClazz : hibernateEntitiesClazz) {
            log.debug("Successfully loaded hibernate entity: {}", hibernateEntitityClazz.getName());
            configuration.addAnnotatedClass(hibernateEntitityClazz);
        }
        configuration.setProperties(properties).configure(C3P0_CFG);
        log.info("Successfully loaded Hibernate configuration.");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void abstractInitializeComponent() {
        if (!Objects.isNull(sessionFactory)) return;
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
            liquibase.update("liquibaseContext");

            sessionFactory = configuration.buildSessionFactory();
            log.info("Successfully created Hibernate session factory instance.");
        } catch (LiquibaseException | SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void executeTrasactQuery(Consumer<Session> onExecute, Consumer<RuntimeException> onException) {
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                onExecute.accept(session);
                session.getTransaction().commit();
            } catch (RuntimeException ex) {
                session.getTransaction().rollback();
                log.error("Something goes wrong. Rollback and return previous DB state...");
                throw ex;
            }
        } catch (RuntimeException ex) {
            onException.accept(ex);
        }
    }
}
