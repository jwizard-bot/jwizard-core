/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: EnvironmentVariable.java
 * Last modified: 13/09/2022, 03:30
 * Project name: java-franek-bot
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

package pl.miloszgilga.franekbotapp.configuration;

import lombok.Getter;
import lombok.AllArgsConstructor;

//----------------------------------------------------------------------------------------------------------------------

@Getter
@AllArgsConstructor
public enum EnvironmentVariable {
    PROD_PREFIX("PROD_"),
    DEV_PREFIX("DEV_"),
    TOKEN("TOKEN"),
    APPLICATION_ID("APPLICATION_ID"),
    DATABASE_CONNECTION_STRING("DATABASE_CONNECTION_STRING"),
    DATABASE_USERNAME("DATABASE_USERNAME"),
    DATABASE_PASSWORD("DATABASE_PASSWORD");

    private final String name;
}
