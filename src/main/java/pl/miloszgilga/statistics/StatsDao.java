/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: StatsDao.java
 * Last modified: 10/04/2023, 14:09
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

package pl.miloszgilga.statistics;

import org.springframework.stereotype.Component;

import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.domain.member_settings.IMemberSettingsRepository;

import static pl.miloszgilga.exception.StatsException.YouHasNoStatsYetInGuildException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Component
public class StatsDao {

    private final BotConfiguration config;
    private final IMemberSettingsRepository settingsRepository;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    StatsDao(BotConfiguration config, IMemberSettingsRepository settingsRepository) {
        this.config = config;
        this.settingsRepository = settingsRepository;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void toggleMemberStatsVisibility(CommandEventWrapper event, boolean isPrivate) {
        settingsRepository
            .findByMember_DiscordIdAndGuild_DiscordId(event.getMemberId(), event.getGuildId())
            .ifPresentOrElse(
                memberSettingsEntity -> {
                    memberSettingsEntity.setStatsPrivate(isPrivate);
                    settingsRepository.save(memberSettingsEntity);
                },
                () -> { throw new YouHasNoStatsYetInGuildException(config, event); }
            );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void turnOnOffMemberStats(CommandEventWrapper event, boolean isTurnOff) {
        settingsRepository
            .findByMember_DiscordIdAndGuild_DiscordId(event.getMemberId(), event.getGuildId())
            .ifPresentOrElse(
                memberSettingsEntity -> {
                    memberSettingsEntity.setStatsDisabled(isTurnOff);
                    settingsRepository.save(memberSettingsEntity);
                },
                () -> { throw new YouHasNoStatsYetInGuildException(config, event); }
            );
    }
}
