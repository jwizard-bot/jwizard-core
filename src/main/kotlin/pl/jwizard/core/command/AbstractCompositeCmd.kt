/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command

import com.jagrosh.jdautilities.menu.Paginator
import net.dv8tion.jda.api.exceptions.PermissionException
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.internal.interactions.component.ButtonImpl
import org.apache.commons.lang3.StringUtils
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.action.ActionComponent
import pl.jwizard.core.command.arg.ArgumentTypeCaster
import pl.jwizard.core.command.arg.CommandArgument
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.exception.AbstractBotException
import pl.jwizard.core.exception.UtilException
import pl.jwizard.core.i18n.I18nLocale
import java.util.concurrent.TimeUnit
import kotlin.reflect.cast

abstract class AbstractCompositeCmd(
	protected val botConfiguration: BotConfiguration,
) {
	protected val guildSettings = botConfiguration.guildSettingsSupplier
	protected val i18nService = botConfiguration.i18nService
	protected val commandsSupplier = botConfiguration.commandsSupplier
	protected val commandReflectLoader = botConfiguration.commandReflectLoader
	protected val radioSupplier = botConfiguration.radioSupplier

	internal fun performCommand(event: CompoundCommandEvent): InteractiveMessage {
		try {
			execute(event)
		} catch (ex: AbstractBotException) {
			event.interactiveMessage.messageEmbeds.clear() // remove all previous messages from queue on error
			val embedMessage = CustomEmbedBuilder(botConfiguration, event).buildErrorMessage(
				placeholder = ex.i18nLocale,
				params = ex.variables
			)
			event.appendEmbedMessage(embedMessage)
		}
		return event.interactiveMessage
	}

	protected fun checkIfCommandModuleIsEnabled(
		event: CompoundCommandEvent,
		module: CommandModule,
	) {
		val moduleState = commandReflectLoader.checkIfModuleIsEnabled(module, event)
		if (!moduleState.isEnabled) {
			throw UtilException.ModuleIsTurnedOffException(event, moduleState.name)
		}
	}

	protected inline fun <reified T : Any> getArg(arg: CommandArgument, event: CompoundCommandEvent): T {
		val (value, type) = event.commandArgs[arg] ?: throw UtilException.UnexpectedException("Argument not found")
		val caster = ArgumentTypeCaster.valueOf(type)
		return T::class.cast(caster.castCallback(value))
	}

	protected fun createDefaultPaginator(items: List<String>, pageSize: Int): Paginator = Paginator.Builder()
		.setColumns(1)
		.setFinalAction {
			try {
				it.clearReactions().queue()
			} catch (ignore: PermissionException) {
			}
		}
		.setItemsPerPage(pageSize)
		.setText(StringUtils.EMPTY)
		.showPageNumbers(true)
		.setColor(EmbedColor.WHITE.color())
		.setEventWaiter(botConfiguration.eventWaiter)
		.allowTextInput(false)
		.waitOnSinglePage(false)
		.setTimeout(botConfiguration.botProperties.pagination.menuAliveSec, TimeUnit.SECONDS)
		.wrapPageEnds(true)
		.setItems(*items.toTypedArray())
		.build()

	private fun createButton(
		actionComponent: ActionComponent,
		style: ButtonStyle,
		placeholder: I18nLocale,
		params: Map<String, Any>,
		lang: String,
	): Button {
		return ButtonImpl(
			actionComponent.name,
			i18nService.getMessage(placeholder, params, lang),
			style,
			false,
			null
		)
	}

	protected fun createDefaultPaginator(items: List<String>): Paginator {
		return createDefaultPaginator(items, botConfiguration.botProperties.pagination.maxElementsPerPage - 1)
	}

	protected fun createButton(
		actionComponent: ActionComponent,
		style: ButtonStyle,
		placeholder: I18nLocale,
		lang: String,
	): Button {
		return createButton(actionComponent, style, placeholder, emptyMap(), lang)
	}

	abstract fun execute(event: CompoundCommandEvent)
}
