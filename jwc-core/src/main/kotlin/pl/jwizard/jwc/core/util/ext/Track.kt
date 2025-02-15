package pl.jwizard.jwc.core.util.ext

import pl.jwizard.jwac.player.track.Track
import pl.jwizard.jwc.core.util.mdLink
import pl.jwizard.jwc.core.util.millisToDTF

val Track.qualifier
	get() = "\"${getTitle(normalized = true)}\""

val Track.mdTitleLink
	get() = mdLink(getTitle(normalized = true), uri)

val Track.titleWithDuration
	get() = "(${millisToDTF(duration)}): ${getTitle(normalized = true)}"

val Track.mdTitleLinkWithDuration
	get() = "(${millisToDTF(duration)}): $mdTitleLink"
