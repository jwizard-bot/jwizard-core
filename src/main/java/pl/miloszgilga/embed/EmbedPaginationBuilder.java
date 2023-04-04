/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: EmbedPaginationBuilder.java
 * Last modified: 18/03/2023, 19:39
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
