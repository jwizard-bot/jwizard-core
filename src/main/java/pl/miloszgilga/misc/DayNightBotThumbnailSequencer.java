/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: DayNightBotThumbnailSequencer.java
 * Last modified: 14/03/2023, 14:38
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

package pl.miloszgilga.misc;

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.managers.AccountManager;
import net.dv8tion.jda.internal.managers.AccountManagerImpl;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.AbstractConfigLoadableComponent;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@Component
public class DayNightBotThumbnailSequencer extends AbstractConfigLoadableComponent {

    private final BotConfiguration config;

    private JDA jda;
    private TimeZone timezone;
    private LocalTime dayTrigger;
    private LocalTime nightTrigger;
    private String dayAvatarPath;
    private String nightAvatarPath;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    DayNightBotThumbnailSequencer(BotConfiguration config) {
        this.config = config;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void abstractLoadConfiguration(Object... params) {
        this.jda = (JDA) params[0];
        this.timezone = TimeZone.getTimeZone(config.getProperty(BotProperty.J_AVATAR_DAY_NIGHT_TIMEZONE));
        this.dayTrigger = LocalTime.parse(String.format("%02d:00:00",
            config.getProperty(BotProperty.J_AVATAR_DAY_TRIGGER, Byte.class)));
        this.nightTrigger = LocalTime.parse(String.format("%02d:00:00",
            config.getProperty(BotProperty.J_AVATAR_NIGHT_TRIGGER, Byte.class)));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void abstractInitializeComponent() {
        if (!config.getProperty(BotProperty.J_AVATAR_DAY_NIGHT_ENABLED, Boolean.class)) return;

        dayAvatarPath = config.getProperty(BotProperty.J_PATH_TO_AVATAR_DAY_MODE);
        nightAvatarPath = config.getProperty(BotProperty.J_PATH_TO_AVATAR_NIGHT_MODE);
        try {
            final Icon dayImage = Icon.from(new File(dayAvatarPath));
            final Icon nightImage = Icon.from(new File(nightAvatarPath));

            final LocalDateTime localNow = LocalDateTime.now(timezone.toZoneId());
            final LocalDateTime localEnd = localNow.plusHours(1).truncatedTo(ChronoUnit.HOURS);
            final long leftIntoFullHour = Duration.between(localNow, localEnd).toMillis();

            cyclicChangeAvatar(dayImage, nightImage);
            config.getThreadPool().scheduleAtFixedRate(() -> cyclicChangeAvatar(dayImage, nightImage),
                leftIntoFullHour, 30, TimeUnit.MINUTES);

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void cyclicChangeAvatar(Icon dayImage, Icon nightImage) {
        final LocalTime now = LocalTime.now(timezone.toZoneId());
        AccountManager accountManager = new AccountManagerImpl(jda.getSelfUser());
        if (now.isAfter(dayTrigger) && now.isBefore(nightTrigger)) {
            accountManager = accountManager.setAvatar(dayImage);
            log.info("Set day avatar image: '{}' for day trigger from {} to {} in timezone '{}'", dayAvatarPath,
                dayTrigger, nightTrigger, timezone.getID());
        } else if (now.isBefore(dayTrigger) && now.isAfter(nightTrigger)) {
            accountManager = accountManager.setAvatar(nightImage);
            log.info("Set night avatar image: '{}' for night trigger from {} to {} in timezone '{}'", nightAvatarPath,
                nightTrigger, dayTrigger, timezone.getID());
        }
        accountManager.queue();
    }
}
