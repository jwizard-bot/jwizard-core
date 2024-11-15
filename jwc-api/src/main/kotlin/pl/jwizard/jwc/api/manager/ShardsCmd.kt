/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.manager

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import pl.jwizard.jwc.api.ManagerCommandBase
import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.interaction.component.RefreshableComponent
import pl.jwizard.jwc.command.interaction.component.RefreshableContent
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nActionSource
import pl.jwizard.jwc.core.i18n.source.I18nSystemSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.command.arg.Argument

/**
 * Command to display and manage shard information for the Discord bot.
 *
 * @param commandEnvironment The environment context for command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.SHARDS)
class ShardsCmd(
	commandEnvironment: CommandEnvironmentBean
) : ManagerCommandBase(commandEnvironment), RefreshableContent<CommandContext> {

	/**
	 * Executes the command logic to display shard information.
	 *
	 * This method initializes the refreshable component, constructs the shard information embed message, and completes
	 * the response by providing the embed and action buttons.
	 *
	 * @param context The context of the command, which contains details of the user interaction.
	 * @param response The future response object used to send the result of the command execution.
	 */
	override fun executeManager(context: CommandContext, response: TFutureResponse) {
		val refreshableComponent = RefreshableComponent(i18nBean, eventQueueBean, this, context)
		refreshableComponent.initEvent()

		val shardsLink = createLinkFromFragment(BotProperty.LINK_FRAGMENT_SHARDS)
		val actionRow = ActionRow.of(
			refreshableComponent.createRefreshButton(context),
			createLinkButton(I18nActionSource.DETAILS, shardsLink, context),
			createLinkButton(I18nActionSource.STATUS, BotProperty.LINK_STATUS, context),
		)
		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(createShardsInfoMessage(context))
			.addActionRows(actionRow)
			.build()
		response.complete(commandResponse)
	}

	/**
	 * Checks whether the command should be executed in private mode. If the command is executed with a "private"
	 * argument set to true, it returns the author's ID to restrict visibility.
	 *
	 * @param context The context of the command execution.
	 * @return The author's ID if the command is private, or null if it is not private.
	 */
	override fun isPrivate(context: CommandContext): Long? {
		val isPrivate = context.getNullableArg<Boolean>(Argument.PRIVATE)
		return if (isPrivate == true) context.author.idLong else null
	}

	/**
	 * Refreshes the shard information content when triggered by a button interaction. This method updates the content
	 * displayed in the message by regenerating the shard information embed.
	 *
	 * @param event The button interaction event triggered by the user.
	 * @param response A mutable list to which the new shard information embed will be added.
	 * @param payload The original command context used to regenerate the shard information.
	 */
	override fun onRefresh(event: ButtonInteractionEvent, response: MutableList<MessageEmbed>, payload: CommandContext) {
		response.add(createShardsInfoMessage(payload))
	}

	/**
	 * Creates the embed message containing detailed shard information. This message includes shard offsets, the number
	 * of queued and running shards, and the average gateway ping.
	 *
	 * @param context The context of the command execution, used to localize and personalize the embed message.
	 * @return A [MessageEmbed] containing shard details.
	 */
	private fun createShardsInfoMessage(context: CommandContext): MessageEmbed {
		val shardsStartOffset = environmentBean.getProperty<Int>(BotProperty.JDA_SHARDING_FRAGMENT_MIN_ID)
		val endStartOffset = environmentBean.getProperty<Int>(BotProperty.JDA_SHARDING_FRAGMENT_MAX_ID)
		return createEmbedMessage(context)
			.setTitle(I18nSystemSource.SHARDS_INFO_HEADER)
			.setKeyValueField(I18nSystemSource.SHARDS_START_OFFSET, shardsStartOffset)
			.setSpace()
			.setKeyValueField(I18nSystemSource.SHARDS_END_OFFSET, endStartOffset)
			.setKeyValueField(I18nSystemSource.SHARDS_OFFSET_LENGTH, (endStartOffset - shardsStartOffset) + 1)
			.setSpace()
			.setKeyValueField(I18nSystemSource.QUEUED_SHARDS, jdaShardManager.queuedShardsCount)
			.setKeyValueField(I18nSystemSource.RUNNING_SHARDS, jdaShardManager.runningShardsCount)
			.setSpace()
			.setKeyValueField(I18nSystemSource.AVG_GATEWAY_PING, "%.2f ms".format(jdaShardManager.averageGatewayPing))
			.setColor(JdaColor.PRIMARY)
			.build()
	}
}
