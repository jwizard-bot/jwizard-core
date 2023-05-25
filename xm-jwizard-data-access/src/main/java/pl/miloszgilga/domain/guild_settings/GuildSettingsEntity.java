/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: GuildSettingsEntity.java
 * Last modified: 16/05/2023, 16:10
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

package pl.miloszgilga.domain.guild_settings;

import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

import org.jmpsl.core.db.AbstractAuditableEntity;

import pl.miloszgilga.domain.guild.GuildEntity;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@NoArgsConstructor
@Table(name = "guild_settings")
public class GuildSettingsEntity extends AbstractAuditableEntity implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @Column(name = "audio_text_channel_id")             private String audioTextChannelId;
    @Column(name = "skip_ratio")                        private Integer skipRatio;
    @Column(name = "time_to_leave_empty_channel")       private Integer timeToLeaveEmptyChannel;
    @Column(name = "time_to_leave_no_tracks_channel")   private Integer timeToLeaveNoTracksChannel;
    @Column(name = "time_to_end_voting")                private Integer timeToEndVoting;
    @Column(name = "player_volume")                     private Integer playerVolume;
    @Column(name = "max_repeats_single_track")          private Integer maxRepeatsSingleTrack;
    @Column(name = "dj_role_name")                      private String djRoleName;
    @Column(name = "i18n_locale")                       private String i18nLocale;

    @OneToOne(cascade = { PERSIST, MERGE, REMOVE }, fetch = LAZY)
    @JoinColumn(name = "guild_id", referencedColumnName = "id")
    private GuildEntity guild;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getAudioTextChannelId() {
        return audioTextChannelId;
    }

    public void setAudioTextChannelId(String musicTextChannelId) {
        this.audioTextChannelId = musicTextChannelId;
    }

    public Integer getSkipRatio() {
        return skipRatio;
    }

    public void setSkipRatio(Integer skipRatio) {
        this.skipRatio = skipRatio;
    }

    public Integer getTimeToLeaveEmptyChannel() {
        return timeToLeaveEmptyChannel;
    }

    public void setTimeToLeaveEmptyChannel(Integer timeToLeaveEmptyChannel) {
        this.timeToLeaveEmptyChannel = timeToLeaveEmptyChannel;
    }

    public Integer getTimeToLeaveNoTracksChannel() {
        return timeToLeaveNoTracksChannel;
    }

    public void setTimeToLeaveNoTracksChannel(Integer timeToLeaveNoTracksChannel) {
        this.timeToLeaveNoTracksChannel = timeToLeaveNoTracksChannel;
    }

    public Integer getTimeToEndVoting() {
        return timeToEndVoting;
    }

    public void setTimeToEndVoting(Integer timeToEndVoting) {
        this.timeToEndVoting = timeToEndVoting;
    }

    public Integer getPlayerVolume() {
        return playerVolume;
    }

    public void setPlayerVolume(Integer playerVolume) {
        this.playerVolume = playerVolume;
    }

    public Integer getMaxRepeatsSingleTrack() {
        return maxRepeatsSingleTrack;
    }

    public void setMaxRepeatsSingleTrack(Integer maxRepeatsSingleTrack) {
        this.maxRepeatsSingleTrack = maxRepeatsSingleTrack;
    }

    public String getDjRoleName() {
        return djRoleName;
    }

    public void setDjRoleName(String djRoleName) {
        this.djRoleName = djRoleName;
    }

    public String getI18nLocale() {
        return i18nLocale;
    }

    public void setI18nLocale(String i18nLocale) {
        this.i18nLocale = i18nLocale;
    }

    GuildEntity getGuild() {
        return guild;
    }

    public void setGuild(GuildEntity guild) {
        this.guild = guild;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "audioTextChannelId=" + audioTextChannelId +
            ", skipRatio=" + skipRatio +
            ", timeToLeaveEmptyChannel=" + timeToLeaveEmptyChannel +
            ", timeToLeaveNoTracksChannel=" + timeToLeaveNoTracksChannel +
            ", timeToEndVoting=" + timeToEndVoting +
            ", playerVolume=" + playerVolume +
            ", maxRepeatsSingleTrack=" + maxRepeatsSingleTrack +
            ", djRoleName=" + djRoleName +
            ", i18nLocale=" + i18nLocale +
            '}';
    }
}
