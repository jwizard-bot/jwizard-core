/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: StatsCommandEntity.java
 * Last modified: 6/8/23, 8:33 PM
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

package pl.miloszgilga.domain.stats_commands;

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
@Table(name = "stats_commands")
public class StatsCommandEntity extends AbstractAuditableEntity implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @Column(name = "gstats_enabled", insertable = false)        private Boolean gstatsEnabled;
    @Column(name = "mstats_enabled", insertable = false)        private Boolean mstatsEnabled;
    @Column(name = "mystats_enabled", insertable = false)       private Boolean mystatsEnabled;
    @Column(name = "statson_enabled", insertable = false)       private Boolean statsonEnabled;
    @Column(name = "statsoff_enabled", insertable = false)      private Boolean statsoffEnabled;
    @Column(name = "pubstats_enabled", insertable = false)      private Boolean pubstatsEnabled;
    @Column(name = "privstats_enabled", insertable = false)     private Boolean privstatsEnabled;
    @Column(name = "resetmstats_enabled", insertable = false)   private Boolean resetmstatsEnabled;
    @Column(name = "resetgstats_enabled", insertable = false)   private Boolean resetgstatsEnabled;

    @OneToOne(cascade = { PERSIST, MERGE, REMOVE }, fetch = LAZY)
    @JoinColumn(name = "guild_id", referencedColumnName = "id")
    private GuildEntity guild;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Boolean getGstatsEnabled() {
        return gstatsEnabled;
    }

    public void setGstatsEnabled(Boolean statsEnabled) {
        this.gstatsEnabled = statsEnabled;
    }

    public Boolean getMstatsEnabled() {
        return mstatsEnabled;
    }

    public void setMstatsEnabled(Boolean mtatsEnabled) {
        this.mstatsEnabled = mtatsEnabled;
    }

    public Boolean getMystatsEnabled() {
        return mystatsEnabled;
    }

    public void setMystatsEnabled(Boolean mystatsEnabled) {
        this.mystatsEnabled = mystatsEnabled;
    }

    public Boolean getStatsonEnabled() {
        return statsonEnabled;
    }

    public void setStatsonEnabled(Boolean statsonEnabled) {
        this.statsonEnabled = statsonEnabled;
    }

    public Boolean getStatsoffEnabled() {
        return statsoffEnabled;
    }

    public void setStatsoffEnabled(Boolean statsoffEnabled) {
        this.statsoffEnabled = statsoffEnabled;
    }

    public Boolean getPubstatsEnabled() {
        return pubstatsEnabled;
    }

    public void setPubstatsEnabled(Boolean pubstatsEnabled) {
        this.pubstatsEnabled = pubstatsEnabled;
    }

    public Boolean getPrivstatsEnabled() {
        return privstatsEnabled;
    }

    public void setPrivstatsEnabled(Boolean privstatsEnabled) {
        this.privstatsEnabled = privstatsEnabled;
    }

    public Boolean getResetmstatsEnabled() {
        return resetmstatsEnabled;
    }

    public void setResetmstatsEnabled(Boolean resetmstatsEnabled) {
        this.resetmstatsEnabled = resetmstatsEnabled;
    }

    public Boolean getResetgstatsEnabled() {
        return resetgstatsEnabled;
    }

    public void setResetgstatsEnabled(Boolean resetgstatsEnabled) {
        this.resetgstatsEnabled = resetgstatsEnabled;
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
            "gstatsEnabled=" + gstatsEnabled +
            ", mstatsEnabled=" + mstatsEnabled +
            ", mystatsEnabled=" + mystatsEnabled +
            ", statsonEnabled=" + statsonEnabled +
            ", statsoffEnabled=" + statsoffEnabled +
            ", pubstatsEnabled=" + pubstatsEnabled +
            ", privstatsEnabled=" + privstatsEnabled +
            ", resetmstatsEnabled=" + resetmstatsEnabled +
            ", resetgstatsEnabled=" + resetgstatsEnabled +
            '}';
    }
}
