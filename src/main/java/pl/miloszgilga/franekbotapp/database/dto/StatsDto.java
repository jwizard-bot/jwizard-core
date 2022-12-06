/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ServerStatsDto.java
 * Last modified: 06.12.2022, 00:35
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

package pl.miloszgilga.franekbotapp.database.dto;

import lombok.*;


@AllArgsConstructor
public class StatsDto {
    private final long addedMess;
    private final long updatedMess;
    private final long addedReacts;

    public String getAddedMess() {
        return Long.toString(addedMess);
    }

    public String getUpdatedMess() {
        return Long.toString(updatedMess);
    }

    public String getAddedReacts() {
        return Long.toString(addedReacts);
    }
}
