/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.Component

data class InteractiveMessage(
	val messageEmbeds: MutableList<MessageEmbed>,
	var actionComponents: MutableList<Component>
) {
	constructor() : this(mutableListOf(), mutableListOf())
}
