/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: PauseResumeTrackEmbedContent.java
 * Last modified: 12/03/2023, 19:55
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

package pl.miloszgilga.dto;

import java.util.Map;

import pl.miloszgilga.core.LocaleSet;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public record PauseTrackEmbedContent(
    LocaleSet localeSet,
    Map<String, Object> localeVariables,
    String pausedTimestamp,
    String estimatedDuration,
    String totalDuration,
    String pausedVisualizationTrack
) {
}
