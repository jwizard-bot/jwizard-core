/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: JDAListenerAdapter.java
 * Last modified: 23/02/2023, 19:25
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

package pl.miloszgilga.core;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.context.annotation.DependsOn;

import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@DependsOn("botConfiguration")
public abstract class JDAListenerAdapter extends ListenerAdapter {

    protected BotConfiguration config;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public JDAListenerAdapter(BotConfiguration config) {
        this.config = config;
    }
}
