package pl.jwizard.jwc.core.radio.spi

import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwl.radio.RadioStation

interface RadioPlaybackMessage {
	fun createPlaybackDataMessage(
		radioStation: RadioStation,
		context: CommandBaseContext,
	): MessageEmbed
}
