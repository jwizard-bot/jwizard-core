/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.util.ext

import pl.jwizard.jwac.player.track.Track
import pl.jwizard.jwc.core.util.mdLink
import pl.jwizard.jwc.core.util.millisToDTF

/**
 * Extension property that generates a formatted string representation of the track, including its title and author.
 *
 * The resulting string is formatted as:
 * "Title (Author)"
 *
 * @return A string representing the track's title and author.
 * @author Miłosz Gilga
 */
val Track.qualifier get() = "\"${getTitle(normalized = true)}\""

/**
 * Extension property that creates a Markdown-formatted link for the track's normalized title, pointing to its URI.
 *
 * The resulting string is formatted as:
 * "[Normalized Title](Track URI)"
 *
 * @return A Markdown link for the track's normalized title.
 * @author Miłosz Gilga
 */
val Track.mdTitleLink get() = mdLink(getTitle(normalized = true), uri)

/**
 * Extension property that provides a normalized title for the track by removing asterisks from the title and including
 * the author's name.
 *
 * The resulting string is formatted as:
 * "Title (Author)"
 *
 * @return The normalized title of the track without asterisks.
 * @author Miłosz Gilga
 */
val Track.titleWithDuration get() = "(${millisToDTF(duration)}): ${getTitle(normalized = true)}"

/**
 * Extension property that generates a string representation of the track's duration and its normalized title.
 *
 * The resulting string is formatted as:
 * "(Duration): Title (Author)"
 *
 * @return A string containing the track's duration formatted as `mm:ss` followed by the normalized title.
 * @author Miłosz Gilga
 */
val Track.mdTitleLinkWithDuration get() = "(${millisToDTF(duration)}): $mdTitleLink"
