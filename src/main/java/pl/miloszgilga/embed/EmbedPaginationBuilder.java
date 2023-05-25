/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: EmbedPaginationBuilder.java
 * Last modified: 04/04/2023, 14:03
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

package pl.miloszgilga.embed;

import com.jagrosh.jdautilities.menu.Paginator;
import net.dv8tion.jda.api.exceptions.PermissionException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Component
public class EmbedPaginationBuilder {

    private final BotConfiguration config;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    EmbedPaginationBuilder(BotConfiguration config) {
        this.config = config;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Paginator createDefaultPaginator(List<String> items) {
        return new Paginator.Builder()
            .setColumns(1)
            .setFinalAction(m -> { try { m.clearReactions().queue(); } catch (PermissionException ignore) {} })
            .setItemsPerPage(config.getProperty(BotProperty.J_PAGINATION_MAX, Integer.class) - 1)
            .setText(StringUtils.EMPTY)
            .showPageNumbers(true)
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .setEventWaiter(config.getEventWaiter())
            .allowTextInput(false)
            .waitOnSinglePage(false)
            .setTimeout(config.getProperty(BotProperty.J_PAGINATION_MENU_IS_ALIVE, Long.class), TimeUnit.SECONDS)
            .wrapPageEnds(true)
            .setItems(items.toArray(String[]::new))
            .build();
    }
}
