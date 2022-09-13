/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ConfigurationLoader.java
 * Last modified: 22/07/2022, 21:56
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

package pl.miloszgilga.franekbotapp.configuration;

import org.slf4j.Logger;
import io.github.cdimascio.dotenv.Dotenv;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import static pl.miloszgilga.franekbotapp.configuration.EnvironmentVariable.*;


public class ConfigurationLoader {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ConfigurationLoader.class);

    private static final String PROD_CONFIG_FILE = "prod-config.json";
    private static final String DEV_CONFIG_FILE = "dev-config.json";
    private static final String DEV_INPUT_ARG = "--dev";

    public static Configuration config;

    private static void loadConfiguration(String configJSON) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream configFile = ConfigurationLoader.class.getResourceAsStream("/config/" + configJSON);
        if (configFile == null) {
            throw new FileNotFoundException(String.format("Plik konfiguracyjny %s nie istnieje!", configJSON));
        }
        config = objectMapper.readValue(new String(configFile.readAllBytes()), Configuration.class);
        logger.info("Konfiguracja z pliku '{}' załadowana pomyślnie", configJSON);
        includeEnvironmentVariablesIntoConfig();
    }

    private static void includeEnvironmentVariablesIntoConfig() {
        //final Dotenv dotenv = Dotenv.configure().systemProperties().load();
        final String envPrefix = config.isDevelopmentMode() ? DEV_PREFIX.getName() : PROD_PREFIX.getName();
        config.getAuthorization().setToken(System.getenv(envPrefix + TOKEN.getName()));
        config.getAuthorization().setApplicationId(System.getenv(envPrefix + APPLICATION_ID.getName()));
        config.getDbConfig().setDatabaseUrl(System.getenv(envPrefix + DATABASE_CONNECTION_STRING.getName()));
        config.getDbConfig().setUsername(System.getenv(envPrefix + DATABASE_USERNAME.getName()));
        config.getDbConfig().setPassword(System.getenv(envPrefix + DATABASE_PASSWORD.getName()));
        logger.info("Konfiguracja zmiennych środowiskowych załatowana pomyślnie.");
    }

    public static void checkIfItsDevelopmentVersion(String[] args) throws IOException {
        if (args.length == 0) {
            loadConfiguration(PROD_CONFIG_FILE);
            System.out.format("%nAplikacja uruchamiana jest w wersji PRODUKCYJNEJ. Zawartość " +
                    "konfiguracyjna pobierana jest z pliku %s.%n", PROD_CONFIG_FILE);
            return;
        }
        if (args.length > 1 || !args[0].equals(DEV_INPUT_ARG)) {
            throw new IllegalArgumentException(String.format("Poprawny argument wejściowy to %s", DEV_INPUT_ARG));
        }
        loadConfiguration(DEV_CONFIG_FILE);
        System.out.format("%nAplikacja uruchamiana jest w wersji DEWELOPERSKIEJ. Zawartość " +
                "konfiguracyjna pobierana jest z pliku %s.%n", DEV_CONFIG_FILE);
    }
}
