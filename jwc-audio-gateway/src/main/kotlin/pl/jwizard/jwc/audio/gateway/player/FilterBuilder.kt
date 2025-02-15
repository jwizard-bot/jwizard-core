package pl.jwizard.jwc.audio.gateway.player

import dev.arbjerg.lavalink.protocol.v4.*
import kotlinx.serialization.json.JsonElement

class FilterBuilder {
	private var volume: Omissible<Float> = Omissible.Omitted()
	private var equalizer: Omissible<List<Band>> = Omissible.Omitted()
	private var karaoke: Omissible<Karaoke?> = Omissible.Omitted()
	private var timescale: Omissible<Timescale?> = Omissible.Omitted()
	private var tremolo: Omissible<Tremolo?> = Omissible.Omitted()
	private var vibrato: Omissible<Vibrato?> = Omissible.Omitted()
	private var distortion: Omissible<Distortion?> = Omissible.Omitted()
	private var rotation: Omissible<Rotation?> = Omissible.Omitted()
	private var channelMix: Omissible<ChannelMix?> = Omissible.Omitted()
	private var lowPass: Omissible<LowPass?> = Omissible.Omitted()
	private var pluginFilters: MutableMap<String, JsonElement> = mutableMapOf()

	fun setVolume(volume: Float) = apply {
		this.volume = volume.toOmissible()
	}

	fun setEqualizer(equalizer: List<Band>) = apply {
		this.equalizer = equalizer.toOmissible()
	}

	fun setEqualizer(band: Int, gain: Float = 1.0F) = apply {
		val bandInstance = Band(band, gain)
		val currentEqualizer = this.equalizer

		this.equalizer = if (currentEqualizer.isPresent()) {
			// if current equalizer is present, get based on band index, otherwise add new band
			// instance to equalizers list
			val mutableEqualizer = currentEqualizer.value.toMutableList()
			val bandIndex = mutableEqualizer.indexOfFirst { it.band == band }
			if (bandIndex > -1) {
				mutableEqualizer[bandIndex] = bandInstance
			} else {
				mutableEqualizer.add(bandInstance)
			}
			mutableEqualizer.toOmissible()
		} else {
			listOf(bandInstance).toOmissible()
		}
	}

	fun setKaraoke(karaoke: Karaoke?) = apply {
		this.karaoke = karaoke.toOmissible()
	}

	fun setTimescale(timescale: Timescale?) = apply {
		this.timescale = Omissible.of(timescale)
	}

	fun setTremolo(tremolo: Tremolo?) = apply {
		this.tremolo = Omissible.of(tremolo)
	}

	fun setVibrato(vibrato: Vibrato?) = apply {
		this.vibrato = Omissible.of(vibrato)
	}

	fun setDistortion(distortion: Distortion?) = apply {
		this.distortion = Omissible.of(distortion)
	}

	fun setRotation(rotation: Rotation?) = apply {
		this.rotation = Omissible.of(rotation)
	}

	fun setChannelMix(channelMix: ChannelMix?) = apply {
		this.channelMix = Omissible.of(channelMix)
	}

	fun setLowPass(lowPass: LowPass?) = apply {
		this.lowPass = Omissible.of(lowPass)
	}

	fun setPluginFilter(name: String, filter: JsonElement) = apply {
		pluginFilters[name] = filter
	}

	fun build() = Filters(
		volume,
		equalizer,
		karaoke,
		timescale,
		tremolo,
		vibrato,
		distortion,
		rotation,
		channelMix,
		lowPass,
		pluginFilters,
	)
}
