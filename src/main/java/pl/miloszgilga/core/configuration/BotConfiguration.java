/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: BotConfiguration.java
 * Last modified: 19/03/2023, 23:07
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

package pl.miloszgilga.core.configuration;

import lombok.Getter;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.managers.AccountManager;
import net.dv8tion.jda.internal.managers.AccountManagerImpl;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import org.yaml.snakeyaml.Yaml;
import org.apache.commons.cli.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.hibernate.PropertyNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.vault.support.JsonMapFlattener;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.*;
import java.util.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import pl.miloszgilga.core.IEnumerableLocaleSet;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@Component
public class BotConfiguration {

    public static final String JPREFIX = "bot";
    private static final String LOG4J_CFG = "logger/log4j2.cfg.xml";
    private static final String LOCALE_BUNDLE_DIR = "lang";
    private static final String LOCALE_BUNDLE_PROP = "messages";
    private static final String ARTIFACT_PROP = "/artifact.properties";

    private final Map<BotProperty, Object> jProperties = new HashMap<>();
    private final Set<String> envProperties = new HashSet<>();
    private final Yaml yaml = new Yaml();

    private final CommandLineParser commandLineParser = new DefaultParser();
    private final HelpFormatter helpFormatter = new HelpFormatter();
    private final Dotenv dotenv = Dotenv.configure().systemProperties().load();

    @Getter(value = AccessLevel.PUBLIC)     private final EventWaiter eventWaiter = new EventWaiter();
    @Getter(value = AccessLevel.PUBLIC)     private String projectVersion;

    private ResourceBundle localeBundle;

    @Getter(value = AccessLevel.PUBLIC)
    private final ScheduledExecutorService threadPool = Executors.newSingleThreadScheduledExecutor(r -> {
        final Thread thread = new Thread(r);
        thread.setDaemon(true);
        return thread;
    });

    public static final List<CastType<?>> CAST_TYPES = List.of(
        new CastType<>(String.class, rawData -> rawData),
        new CastType<>(Boolean.class, Boolean::valueOf),
        new CastType<>(Integer.class, Integer::valueOf),
        new CastType<>(Short.class, Short::valueOf),
        new CastType<>(Byte.class, Byte::valueOf),
        new CastType<>(Float.class, Float::valueOf),
        new CastType<>(Long.class, Long::valueOf)
    );

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void loadConfiguration(String[] args) {
        Configurator.initialize(null, LOG4J_CFG);
        final AppMode appMode = extractModeFromArguments(args);
        String language;
        try {
            final InputStream inputStream = new FileInputStream(appMode.getConfigFile());

            final Map<String, String> propertiesMap = JsonMapFlattener.flattenToStringMap(yaml.load(inputStream));
            for (final Map.Entry<String, String> entry : propertiesMap.entrySet()) {
                final EnvPropertyHolder envProp = BotProperty.getEnvProperty(entry.getKey(), appMode);
                if (!Objects.isNull(envProp) && entry.getValue().equalsIgnoreCase(envProp.placeholder())) {
                    String envValue = System.getenv(envProp.rawProp());
                    if (Objects.isNull(envValue)) envValue = dotenv.get(envProp.rawProp());
                    if (Objects.isNull(envValue)) {
                        throw new RuntimeException("Env property " + envProp.rawProp() + " not found.");
                    }
                    entry.setValue(envValue);
                    envProperties.add(envProp.rawProp());
                }
                jProperties.put(BotProperty.getBaseName(entry.getKey()), entry.getValue());
            }
            inputStream.close();

            final short defaultVolumeUnits = getProperty(BotProperty.J_DEFAULT_PLAYER_VOLUME_UNITS, Short.class);
            if (defaultVolumeUnits < 0 || defaultVolumeUnits > 150) {
                throw new IllegalArgumentException("Default player volume units must be between 0 and 150.");
            }
            language = getProperty(BotProperty.J_SELECTED_LOCALE);

            final ClassLoader loader = new URLClassLoader(new URL[]{ new File(LOCALE_BUNDLE_DIR).toURI().toURL() });
            localeBundle = ResourceBundle.getBundle(LOCALE_BUNDLE_PROP, new Locale(language), loader);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        log.info("Primary language for bot '{}' was successfully loaded", language);
        log.info("Bot configuration for '{}' version was successfully loaded", appMode.getAlias());
        log.info("Successfully loaded variables from '.env' file: {}", envProperties);
        log.info("Slash commands in application was turned {}. To change, set 'slash-commands.enabled' property.",
            getProperty(BotProperty.J_SLASH_COMMANDS_ENABLED, Boolean.class) ? "ON" : "OFF");
        log.info("Instantiate application Spring Context Container...");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void printFancyTitle() {
        if (!getProperty(BotProperty.J_SHOW_FANCY_TITLE, Boolean.class)) return;
        try {
            final InputStream fileStream = new FileInputStream(getProperty(BotProperty.J_FANCY_TITLE_PATH));
            try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileStream))) {
                String line;
                while (!Objects.isNull(line = bufferedReader.readLine())) {
                    System.out.println(line);
                }
                System.out.println();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void printAdditionalInformations() {
        log.info("Starting application...");
        log.info("Detected Java Virtual Machine version (JVM): '{}'", System.getProperty("java.runtime.version"));
        if (!System.getProperty("java.vm.name").contains("64")) {
            log.warn("Found not supported Java version. Recommended Java version is 64bit.");
        }
        try {
            final Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream(ARTIFACT_PROP));

            final String versionProp = (String) properties.get("project.version");
            if (Objects.isNull(versionProp)) throw new PropertyNotFoundException("Property project.version not found.");
            projectVersion = versionProp;
            log.info("Application version: '{}'", versionProp);

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setTitleAndIcon(JDA jda) {
        try {
            final AccountManager accountManager = new AccountManagerImpl(jda.getSelfUser())
                .setName(getProperty(BotProperty.J_NAME));

            final boolean isDayNightModeOn = getProperty(BotProperty.J_AVATAR_DAY_NIGHT_ENABLED, Boolean.class);
            if (!getProperty(BotProperty.J_HAS_AVATAR, Boolean.class) || isDayNightModeOn) {
                accountManager.queue();
                return;
            }
            final String fileName = getProperty(BotProperty.J_PATH_TO_AVATAR);
            accountManager
                .setAvatar(Icon.from(new File(fileName)))
                .queue();
            log.info("Successfully set application avatar from file: {}", fileName);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private AppMode extractModeFromArguments(String[] args) {
        final Options options = new Options();
        try {
            final Option option = Option.builder("m").longOpt("mode").hasArg()
                .required(true)
                .desc("Select application mode (dev/prod)")
                .build();
            options.addOption(option);
            final CommandLine commandLine = commandLineParser.parse(options, args, true);
            if (commandLine.hasOption("m")) {
                if (commandLine.getOptionValue("m").equalsIgnoreCase(AppMode.DEV.getMode())) return AppMode.DEV;
                return AppMode.PROD;
            }
            return AppMode.DEV;
        } catch (ParseException ex) {
            helpFormatter.printHelp("Usage:", options);
            throw new RuntimeException(ex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unchecked")
    public <T> T getProperty(BotProperty property, Class<T> castClazz) {
        return jProperties.entrySet().stream()
            .filter(p -> p.getKey().equals(property))
            .findFirst()
            .map(p -> (T) CAST_TYPES.stream()
                .filter(c -> c.typeClazz().isAssignableFrom(castClazz))
                .findFirst()
                .map(t -> t.cast().apply((String)p.getValue()))
                .orElseThrow(() -> new RuntimeException("Unsupported casting type."))
            )
            .orElseThrow(() -> new RuntimeException("Property is not declared."));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getProperty(BotProperty property) {
        return getProperty(property, String.class);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getLocaleText(IEnumerableLocaleSet localeSet, Map<String, Object> params) {
        if (Objects.isNull(localeSet)) return StringUtils.EMPTY;
        try {
            String resourceText = localeBundle.getString(localeSet.getHolder());
            if (resourceText.isBlank()) {
                return localeSet.getHolder();
            }
            for (final Map.Entry<String, Object> param : params.entrySet()) {
                resourceText = resourceText.replace("{{" + param.getKey() + "}}", String.valueOf(param.getValue()));
            }
            return resourceText;
        } catch (MissingResourceException ex) {
            return localeSet.getHolder();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getLocaleText(IEnumerableLocaleSet localeSet) {
        return getLocaleText(localeSet, Map.of());
    }
}
