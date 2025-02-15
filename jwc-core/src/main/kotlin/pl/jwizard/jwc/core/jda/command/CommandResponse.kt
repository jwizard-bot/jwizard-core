package pl.jwizard.jwc.core.jda.command

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.utils.FileUpload
import net.dv8tion.jda.api.utils.messages.MessagePollData
import java.io.InputStream

class CommandResponse private constructor(
	val embedMessages: List<MessageEmbed>,
	val actionRows: List<ActionRow>,
	val pool: MessagePollData?,
	val files: List<FileUpload>,
	val disposeComponents: Boolean,
	val afterSendAction: (Message) -> Unit,
) {
	fun copy(
		embedMessages: List<MessageEmbed>,
		actionRows: List<ActionRow>,
	) = CommandResponse(embedMessages, actionRows, pool, files, disposeComponents, afterSendAction)

	class Builder {
		private var embedMessages: List<MessageEmbed> = emptyList()
		private var actionRows: List<ActionRow> = emptyList()
		private var pool: MessagePollData? = null
		private var files: List<FileUpload> = emptyList()
		private var disposeComponents: Boolean = true
		private var onSendAction: (Message) -> Unit = {}

		fun addEmbedMessages(vararg embedMessages: MessageEmbed) = apply {
			this.embedMessages = embedMessages.toList()
		}

		fun addActionRows(vararg actionRows: ActionRow?) = apply {
			this.actionRows = actionRows.filterNotNull().filter { !it.isEmpty }.toList()
		}

		fun addPool(pool: MessagePollData) = apply {
			this.pool = pool
		}

		fun addFiles(files: Map<String?, InputStream?>) = apply {
			this.files = files.entries
				.filter { it.key != null && it.value != null }
				.map { (fileName, stream) -> FileUpload.fromData(stream!!, fileName!!) }
		}

		fun disposeComponents(disposeComponents: Boolean) = apply {
			this.disposeComponents = disposeComponents
		}

		fun onSendAction(onSendAction: (Message) -> Unit) = apply {
			this.onSendAction = onSendAction
		}

		fun build() =
			CommandResponse(embedMessages, actionRows, pool, files, disposeComponents, onSendAction)
	}
}
