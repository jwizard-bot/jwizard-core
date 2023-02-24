/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: JEnvProperty.java
 * Last modified: 23/02/2023, 01:08
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

package pl.miloszgilga.core.configuration;

import lombok.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
enum EnvProperty {
    TOKEN           ("TOKEN"),
    APP_ID          ("APP_ID"),
    DB_JDBC         ("DB_JDBC"),
    DB_USERNAME     ("DB_USERNAME"),
    DB_PASSWORD     ("DB_PASSWORD");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String name;
}
