/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command

import java.util.concurrent.TimeUnit
import kotlin.reflect.cast
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.arg.ArgumentTypeCaster
import pl.jwizard.core.command.arg.CommandArgument
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.exception.AbstractBotException
import pl.jwizard.core.exception.UtilException
import com.jagrosh.jdautilities.menu.Paginator
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.exceptions.PermissionException

abstract class AbstractCompositeCmd(
	protected val botConfiguration: BotConfiguration,
) {
	protected val guildSettings = botConfiguration.guildSettings
	protected val i18nService = botConfiguration.i18nService
	protected val commandLoader = botConfiguration.commandLoader

	internal fun performCommand(event: CompoundCommandEvent): MutableList<MessageEmbed> {
		try {
			execute(event)
		} catch (ex: AbstractBotException) {
			event.messageEmbeds.clear()
			val embedMessage = CustomEmbedBuilder(event, botConfiguration).buildErrorMessage(
				placeholder = ex.i18nLocale,
				params = ex.variables
			)
			event.appendEmbedMessage(embedMessage)
		}
		return event.messageEmbeds
	}

	protected fun checkIfCommandModuleIsEnabled(
		event: CompoundCommandEvent,
		module: CommandModule,
	) {
		val guildDetails = guildSettings.getGuildProperties(event.guildId)
		if (!guildDetails.enabledModules.contains(module.moduleName)) {
			throw UtilException.ModuleIsTurnedOffException(
				event,
				moduleName = botConfiguration.commandLoader.getModuleBaseLangInGuildId(event.guildId, module),
			)
		}
	}

	@Suppress("UNCHECKED_CAST")
	protected fun <T> getArg(arg: CommandArgument, event: CompoundCommandEvent): T {
		val (value, type) = event.commandArgs[arg] ?: throw UtilException.UnexpectedException("Argument not found")
		val caster = ArgumentTypeCaster.valueOf(type)
		return caster.clazz.cast(caster.castCallback(value)) as T
	}

	protected fun createDefaultPaginator(items: List<String>): Paginator = Paginator.Builder()
		.setColumns(1)
		.setFinalAction {
			try {
				it.clearReactions().queue()
			} catch (ignore: PermissionException) {
			}
		}
		.setItemsPerPage(botConfiguration.botProperties.pagination.maxElementsPerPage - 1)
		.setText("")
		.showPageNumbers(true)
		.setColor(EmbedColor.WHITE.color())
		.setEventWaiter(botConfiguration.eventWaiter)
		.allowTextInput(false)
		.waitOnSinglePage(false)
		.setTimeout(botConfiguration.botProperties.pagination.menuAliveSec, TimeUnit.SECONDS)
		.wrapPageEnds(true)
		.setItems(*items.toTypedArray())
		.build()

	abstract fun execute(event: CompoundCommandEvent)
}
