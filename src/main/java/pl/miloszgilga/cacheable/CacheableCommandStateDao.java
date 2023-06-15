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

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.cache.annotation.CachePut;

import java.util.Arrays;

import org.jmpsl.core.db.AbstractAuditableEntity;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.command_proxy.*;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.dto.CommandWithProxyDto;
import pl.miloszgilga.exception.CommandException;
import pl.miloszgilga.exception.CommandStateException;
import pl.miloszgilga.core.configuration.BotConfiguration;

import pl.miloszgilga.domain.dj_commands.DjCommandEntity;
import pl.miloszgilga.domain.dj_commands.IDjCommandRepository;
import pl.miloszgilga.domain.music_commands.MusicCommandEntity;
import pl.miloszgilga.domain.music_commands.IMusicCommandRepository;
import pl.miloszgilga.domain.other_commands.OtherCommandEntity;
import pl.miloszgilga.domain.other_commands.IOtherCommandRepository;
import pl.miloszgilga.domain.owner_commands.OwnerCommandEntity;
import pl.miloszgilga.domain.owner_commands.IOwnerCommandRepository;
import pl.miloszgilga.domain.stats_commands.StatsCommandEntity;
import pl.miloszgilga.domain.stats_commands.IStatsCommandRepository;
import pl.miloszgilga.domain.vote_commands.VoteCommandEntity;
import pl.miloszgilga.domain.vote_commands.IVoteCommandRepository;

import static pl.miloszgilga.exception.CommandException.UnexpectedException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Component
@RequiredArgsConstructor
public class CacheableCommandStateDao {

    private final BotConfiguration config;

    private final IDjCommandRepository djCommandRepository;
    private final IMusicCommandRepository musicCommandRepository;
    private final IOtherCommandRepository otherCommandRepository;
    private final IOwnerCommandRepository ownerCommandRepository;
    private final IStatsCommandRepository statsCommandRepository;
    private final IVoteCommandRepository voteCommandRepository;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @CachePut(cacheNames = "GuildDjCommandsStateCache", key = "#p0.guild.id")
    public DjCommandEntity toggleDjCommandState(
        CommandEventWrapper event, IBotCommandProxy<DjCommandEntity>[] allValues, BotCommand cmd, boolean enabled
    ) {
        return setCachedValue(new CachedCommandStateData<>(djCommandRepository, cmd, allValues), event, enabled);
    }

    @CachePut(cacheNames = "GuildMusicCommandsStateCache", key = "#p0.guild.id")
    public MusicCommandEntity toggleMusicCommandState(
        CommandEventWrapper event, IBotCommandProxy<MusicCommandEntity>[] allValues, BotCommand cmd, boolean enabled
    ) {
        return setCachedValue(new CachedCommandStateData<>(musicCommandRepository, cmd, allValues), event, enabled);
    }

    @CachePut(cacheNames = "GuildOtherCommandsStateCache", key = "#p0.guild.id")
    public OtherCommandEntity toggleOtherCommandState(
        CommandEventWrapper event, IBotCommandProxy<OtherCommandEntity>[] allValues, BotCommand cmd, boolean enabled
    ) {
        return setCachedValue(new CachedCommandStateData<>(otherCommandRepository, cmd, allValues), event, enabled);
    }

    @CachePut(cacheNames = "GuildOwnerCommandsStateCache", key = "#p0.guild.id")
    public OwnerCommandEntity toggleOwnerCommandState(
        CommandEventWrapper event, IBotCommandProxy<OwnerCommandEntity>[] allValues, BotCommand cmd, boolean enabled
    ) {
        return setCachedValue(new CachedCommandStateData<>(ownerCommandRepository, cmd, allValues), event, enabled);
    }

    @CachePut(cacheNames = "GuildStatsCommandsStateCache", key = "#p0.guild.id")
    public StatsCommandEntity toggleStatsCommandState(
        CommandEventWrapper event, IBotCommandProxy<StatsCommandEntity>[] allValues, BotCommand cmd, boolean enabled
    ) {
        return setCachedValue(new CachedCommandStateData<>(statsCommandRepository, cmd, allValues), event, enabled);
    }

    @CachePut(cacheNames = "GuildVoteCommandsStateCache", key = "#p0.guild.id")
    public VoteCommandEntity toggleVoteCommandState(
        CommandEventWrapper event, IBotCommandProxy<VoteCommandEntity>[] allValues, BotCommand cmd, boolean enabled
    ) {
        return setCachedValue(new CachedCommandStateData<>(voteCommandRepository, cmd, allValues), event, enabled);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private <T extends AbstractAuditableEntity> T setCachedValue(
        CachedCommandStateData<T> data, CommandEventWrapper event, boolean enabled
    ) {
        final IBotCommandProxy<T> proxy = Arrays.stream(data.allValues())
            .filter(p -> p.getCommand().equals(data.passedCommand()))
            .findFirst()
            .orElseThrow(() -> new CommandException.UnexpectedException(config, event));

        final T entity = data.commandRepository()
            .findByGuild_DiscordId(event.getGuildId())
            .orElseThrow(() -> new UnexpectedException(config, event));

        final boolean isRemoteEnabled = proxy.getIsEnabled().apply(entity);
        if (enabled && isRemoteEnabled) {
            throw new CommandStateException.CommandIsAlreadyTurnedOnException(config, event, proxy.getCommand());
        }
        if (!enabled && !isRemoteEnabled) {
            throw new CommandStateException.CommandIsAlreadyTurnedOffException(config, event, proxy.getCommand());
        }
        proxy.getSetValue().accept(entity, enabled);
        return entity;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void findCategoryWithCommandAndSave(CommandWithProxyDto payload, CommandEventWrapper event, boolean enabled) {
        switch (payload.category()) {
            case DJ -> djCommandRepository
                .save(toggleDjCommandState(event, BotDjCommandProxy.values(), payload.command(), enabled));
            case MUSIC -> musicCommandRepository
                .save(toggleMusicCommandState(event, BotMusicCommandProxy.values(), payload.command(), enabled));
            case OTHERS -> otherCommandRepository
                .save(toggleOtherCommandState(event, BotOtherCommandProxy.values(), payload.command(), enabled));
            case OWNER -> ownerCommandRepository
                .save(toggleOwnerCommandState(event, BotOwnerCommandProxy.values(), payload.command(), enabled));
            case STATS -> statsCommandRepository
                .save(toggleStatsCommandState(event, BotStatsCommandProxy.values(), payload.command(), enabled));
            case VOTE -> voteCommandRepository
                .save(toggleVoteCommandState(event, BotVoteCommandProxy.values(), payload.command(), enabled));
        }
    }
}
