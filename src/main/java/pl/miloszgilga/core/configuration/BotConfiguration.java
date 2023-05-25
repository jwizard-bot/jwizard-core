/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: BotConfiguration.java
 * Last modified: 17/05/2023, 01:26
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

package pl.miloszgilga.core.configuration;

import lombok.Getter;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.managers.AccountManager;
import net.dv8tion.jda.internal.managers.AccountManagerImpl;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import org.springframework.context.annotation.Lazy;
import org.yaml.snakeyaml.Yaml;
import org.apache.commons.lang3.StringUtils;
import io.github.cdimascio.dotenv.Dotenv;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.vault.support.JsonMapFlattener;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import pl.miloszgilga.core.IEnumerableLocaleSet;
import pl.miloszgilga.core.remote.RemoteProperty;
import pl.miloszgilga.core.remote.RemotePropertyHandler;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@Component
public class BotConfiguration {

    public static final String JPREFIX = "bot";
    private static final String LOCALE_BUNDLE_PROP = "i18n-jda/messages";
    private static final String ARTIFACT_PROP = "/artifact.properties";

    private final Map<BotProperty, Object> jProperties = new HashMap<>();
    private final Set<String> envProperties = new HashSet<>();
    private final Yaml yaml = new Yaml();

    private final Dotenv dotenv = Dotenv.configure().systemProperties().load();

    @Getter(value = AccessLevel.PUBLIC)     private final EventWaiter eventWaiter = new EventWaiter();
    @Getter(value = AccessLevel.PUBLIC)     private String projectVersion;

    private final Environment environment;
    private final RemotePropertyHandler handler;

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

    BotConfiguration(Environment environment, @Lazy RemotePropertyHandler handler) {
        this.environment = environment;
        this.handler = handler;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void loadConfiguration() {
        final AppMode appMode = extractModeFromArguments();
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
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        log.info("Bot configuration for '{}' version was successfully loaded", appMode.getAlias());
        log.info("Successfully loaded variables from '.env' file: {}", envProperties);
        log.info("Slash commands in application was turned {}. To change, set 'slash-commands.enabled' property.",
            getProperty(BotProperty.J_SLASH_COMMANDS_ENABLED, Boolean.class) ? "ON" : "OFF");
        log.info("Instantiate Spring Context Container...");
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
            if (Objects.isNull(versionProp)) throw new IllegalStateException("Property project.version not found.");
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

    private AppMode extractModeFromArguments() {
        final String[] springProfile = environment.getActiveProfiles();
        if (springProfile.length > 1) {
            throw new IllegalStateException("Application only support one spring profile.");
        }
        return AppMode.findModeBaseSpringProfile(springProfile[0]);
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

    public String getLocaleText(IEnumerableLocaleSet localeSet, Guild guild, Map<String, Object> params) {
        if (Objects.isNull(localeSet)) return StringUtils.EMPTY;
        try {
            String language = getProperty(BotProperty.J_SELECTED_LOCALE);
            if (!Objects.isNull(guild)) {
                language = handler.getPossibleRemoteProperty(RemoteProperty.R_SELECTED_LOCALE, guild);
            }
            final ResourceBundle localeBundle = ResourceBundle.getBundle(LOCALE_BUNDLE_PROP, new Locale(language));

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

    public String getLocaleText(IEnumerableLocaleSet localeSet, Guild guild) {
        return getLocaleText(localeSet, guild, Map.of());
    }

    public String getLocaleText(IEnumerableLocaleSet localeSet, Map<String, Object> params) {
        return getLocaleText(localeSet, null, params);
    }

    public String getLocaleText(IEnumerableLocaleSet localeSet) {
        return getLocaleText(localeSet, null, Map.of());
    }
}
