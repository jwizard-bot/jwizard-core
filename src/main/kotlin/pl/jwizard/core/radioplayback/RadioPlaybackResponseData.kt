/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.radioplayback

data class RadioPlaybackResponseData(
	val title: String,
	val trackDuration: String?,
	val nextPlay: String?,
	val toNextPlayDuration: String?,
	val streamThumbnailUrl: String?,
	val percentageBar: String?,
	val providedBy: String,
) {
	constructor(title: String, streamThumbnailUrl: String?, providedBy: String) : this(
		title,
		null,
		null,
		null,
		streamThumbnailUrl,
		null,
		providedBy
	)

	constructor(title: String, trackDuration: String?, streamThumbnailUrl: String?, providedBy: String) : this(
		title,
		trackDuration,
		null,
		null,
		streamThumbnailUrl,
		null,
		providedBy
	)
}
