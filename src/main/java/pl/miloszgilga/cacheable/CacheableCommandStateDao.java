/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CacheableCommandStateDao.java
 * Last modified: 6/15/23, 4:38 PM
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

package pl.miloszgilga.cacheable;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;

import org.jmpsl.core.db.AbstractAuditableEntity;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.misc.JDALog;
import pl.miloszgilga.command_proxy.*;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.dto.CommandWithProxyDto;
import pl.miloszgilga.core.configuration.BotConfiguration;

import pl.miloszgilga.domain.dj_commands.IDjCommandRepository;
import pl.miloszgilga.domain.music_commands.IMusicCommandRepository;
import pl.miloszgilga.domain.playlist_commands.IPlaylistCommandRepository;
import pl.miloszgilga.domain.other_commands.IOtherCommandRepository;
import pl.miloszgilga.domain.owner_commands.IOwnerCommandRepository;
import pl.miloszgilga.domain.stats_commands.IStatsCommandRepository;
import pl.miloszgilga.domain.vote_commands.IVoteCommandRepository;

import static pl.miloszgilga.exception.CommandException.UnexpectedException;
import static pl.miloszgilga.exception.CommandStateException.CommandIsAlreadyTurnedOnException;
import static pl.miloszgilga.exception.CommandStateException.CommandIsAlreadyTurnedOffException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheableCommandStateDao {

    private final BotConfiguration config;
    private final CacheManager cacheManager;

    private final IDjCommandRepository djCommandRepository;
    private final IMusicCommandRepository musicCommandRepository;
    private final IPlaylistCommandRepository playlistCommandRepository;
    private final IOtherCommandRepository otherCommandRepository;
    private final IOwnerCommandRepository ownerCommandRepository;
    private final IStatsCommandRepository statsCommandRepository;
    private final IVoteCommandRepository voteCommandRepository;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private <T extends AbstractAuditableEntity> T setCachedValue(
        CachedCmdStateData<T> data, CommandEventWrapper event, boolean enabled
    ) {
        final IBotCommandProxy<T> proxy = retrieveProxyCommand(data, event);
        final T entity = data.commandRepository()
            .findByGuild_DiscordId(event.getGuildId())
            .orElseThrow(() -> new UnexpectedException(config, event));

        final boolean isRemoteEnabled = proxy.getIsEnabled().apply(entity);
        if (enabled && isRemoteEnabled) {
            throw new CommandIsAlreadyTurnedOnException(config, event, proxy.getCommand());
        }
        if (!enabled && !isRemoteEnabled) {
            throw new CommandIsAlreadyTurnedOffException(config, event, proxy.getCommand());
        }
        proxy.getSetValue().accept(entity, enabled);

        final Cache cache = cacheManager.getCache(proxy.getCacheProxyName());
        if (!Objects.isNull(cache)) {
            cache.put(event.getGuildId(), entity);
        }
        JDALog.info(log, event, "Command '%s' was successfully turned %s", proxy.getCommand().getName(),
            enabled ? "ON" : "OFF");
        return entity;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private <T extends AbstractAuditableEntity> boolean checkIfCommandIsEnabled(
        CachedCmdStateData<T> data, CommandEventWrapper event
    ) {
        final IBotCommandProxy<T> proxy = retrieveProxyCommand(data, event);
        final T entity = data.commandRepository()
            .findByGuild_DiscordId(event.getGuildId())
            .orElseThrow(() -> new UnexpectedException(config, event));

        return proxy.getIsEnabled().apply(entity);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private <T extends AbstractAuditableEntity> IBotCommandProxy<T> retrieveProxyCommand(
        CachedCmdStateData<T> data, CommandEventWrapper event
    ) {
        return Arrays.stream(data.allValues())
            .filter(p -> p.getCommand().equals(data.passedCommand()))
            .findFirst()
            .orElseThrow(() -> new UnexpectedException(config, event));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void findCategoryWithCommandAndSave(CommandWithProxyDto payload, CommandEventWrapper event, boolean enabled) {
        final BotCommand cmd = payload.command();
        switch (payload.category()) {
            case DJ -> djCommandRepository.save(setCachedValue(new CachedCmdStateData<>(
                djCommandRepository, cmd, BotDjCommandProxy.values()), event, enabled));
            case MUSIC -> musicCommandRepository.save(setCachedValue(new CachedCmdStateData<>(
                musicCommandRepository, cmd, BotMusicCommandProxy.values()), event, enabled));
            case PLAYLIST -> playlistCommandRepository.save(setCachedValue(new CachedCmdStateData<>(
                playlistCommandRepository, cmd, BotPlaylistCommandProxy.values()), event, enabled));
            case OTHERS -> otherCommandRepository.save(setCachedValue(new CachedCmdStateData<>(
                otherCommandRepository, cmd, BotOtherCommandProxy.values()), event, enabled));
            case OWNER -> ownerCommandRepository.save(setCachedValue(new CachedCmdStateData<>(
                ownerCommandRepository, cmd, BotOwnerCommandProxy.values()), event, enabled));
            case STATS -> statsCommandRepository.save(setCachedValue(new CachedCmdStateData<>(
                statsCommandRepository, cmd, BotStatsCommandProxy.values()), event, enabled));
            case VOTE -> voteCommandRepository.save(setCachedValue(new CachedCmdStateData<>(
                voteCommandRepository, cmd, BotVoteCommandProxy.values()), event, enabled));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean checkIfCommandIsEnabledAndReturn(CommandWithProxyDto payload, CommandEventWrapper event) {
        final BotCommand cmd = payload.command();
        return switch (payload.category()) {
            case DJ -> checkIfCommandIsEnabled(new CachedCmdStateData<>(
                djCommandRepository, cmd, BotDjCommandProxy.values()), event);
            case MUSIC -> checkIfCommandIsEnabled(new CachedCmdStateData<>(
                musicCommandRepository, cmd, BotMusicCommandProxy.values()), event);
            case PLAYLIST -> checkIfCommandIsEnabled(new CachedCmdStateData<>(
                playlistCommandRepository, cmd, BotPlaylistCommandProxy.values()), event);
            case OTHERS -> checkIfCommandIsEnabled(new CachedCmdStateData<>(
                otherCommandRepository, cmd, BotOtherCommandProxy.values()), event);
            case OWNER -> checkIfCommandIsEnabled(new CachedCmdStateData<>(
                ownerCommandRepository, cmd, BotOwnerCommandProxy.values()), event);
            case STATS -> checkIfCommandIsEnabled(new CachedCmdStateData<>(
                statsCommandRepository, cmd, BotStatsCommandProxy.values()), event);
            case VOTE -> checkIfCommandIsEnabled(new CachedCmdStateData<>(
                voteCommandRepository, cmd, BotVoteCommandProxy.values()), event);
        };
    }
}
