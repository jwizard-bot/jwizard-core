/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.util.ext

import dev.arbjerg.lavalink.client.player.Track
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
val Track.qualifier get() = "\"${info.title} (${info.author})\""

/**
 * Extension property that creates a Markdown-formatted link for the track's normalized title, pointing to its URI.
 *
 * The resulting string is formatted as:
 * "[Normalized Title](Track URI)"
 *
 * @return A Markdown link for the track's normalized title.
 * @author Miłosz Gilga
 */
val Track.mdTitleLink get() = mdLink(normalizedTitle, info.uri)

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
val Track.normalizedTitle get() = "${info.title.replace("*", "")} (${info.author})"

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
val Track.titleWithDuration get() = "(${millisToDTF(duration)}): $normalizedTitle"

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

/**
 * Extension property that retrieves the URL of the track's artwork. This URL can be used to display the track's
 * thumbnail image.
 *
 * @return The artwork URL of the track.
 * @author Miłosz Gilga
 */
val Track.thumbnailUrl get() = info.artworkUrl

/**
 *
 * @return
 * @author Miłosz Gilga
 */
val Track.duration get() = info.length
