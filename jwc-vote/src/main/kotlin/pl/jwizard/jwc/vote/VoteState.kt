/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.vote

import net.dv8tion.jda.api.entities.Member
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * A class that manages the state of a vote, including tracking individual votes, whether a vote has passed, and the
 * percentage ratio required for the vote to pass.
 *
 * @param percentageRatio The percentage of positive votes required for the vote to pass.
 * @author Miłosz Gilga
 */
class VoteState(private val percentageRatio: Int) {

	/**
	 * Counter for votes in favor (YES votes).
	 */
	private val forYes = AtomicInteger(0)

	/**
	 * Counter for votes against (NO votes).
	 */
	private val forNo = AtomicInteger(0)

	/**
	 * Indicates whether the vote has passed.
	 */
	private val passed = AtomicBoolean(false)

	/**
	 * A set of member IDs that have already voted, used to prevent re-voting.
	 */
	private val votingMemberIds = mutableSetOf<Long>()

	/**
	 * Provides a pair representing the count of YES and NO votes.
	 */
	val votes
		get() = Pair(forYes.get(), forNo.get())

	/**
	 * Indicates whether the vote has passed, based on the current state.
	 */
	val isPassed
		get() = passed.get()

	/**
	 * Checks if the provided member has already voted.
	 *
	 * @param member The member to check.
	 * @return `true` if the member has already voted, `false` otherwise.
	 */
	fun isMemberReVoting(member: Member) = votingMemberIds.contains(member.idLong)

	/**
	 * Swaps a vote from YES to NO, decrementing YES votes and incrementing NO votes.
	 *
	 * @return A pair representing the vote change (from "YES" to "NO").
	 */
	fun swapYesToNoVote(): Pair<String, String> {
		forYes.decrementAndGet()
		forNo.incrementAndGet()
		return Pair("YES", "NO")
	}

	/**
	 * Swaps a vote from NO to YES, decrementing NO votes and incrementing YES votes.
	 *
	 * @return A pair representing the vote change (from "NO" to "YES").
	 */
	fun swapNoToYesVote(): Pair<String, String> {
		forNo.decrementAndGet()
		forYes.incrementAndGet()
		return Pair("NO", "YES")
	}

	/**
	 * Adds a YES vote for the specified member.
	 *
	 * @param member The member who voted YES.
	 * @return "YES" to indicate the type of vote cast.
	 */
	fun addYesVote(member: Member): String {
		forYes.incrementAndGet()
		votingMemberIds.add(member.idLong)
		return "YES"
	}

	/**
	 * Adds a NO vote for the specified member.
	 *
	 * @param member The member who voted NO.
	 * @return "NO" to indicate the type of vote cast.
	 */
	fun addNoVote(member: Member): String {
		forNo.incrementAndGet()
		votingMemberIds.add(member.idLong)
		return "No"
	}

	/**
	 * Checks if the YES votes have reached the percentage required for the vote to pass.
	 *
	 * @param total The total number of votes cast.
	 * @return `true` if the required percentage of YES votes is reached, `false` otherwise.
	 */
	fun isPassedPositive(total: Int): Boolean {
		val isEnded = getRatio(forYes.get(), total) >= percentageRatio
		if (isEnded) {
			passed.set(true)
		}
		return isEnded
	}

	/**
	 * Checks if the NO votes have reached the percentage required for the vote to fail.
	 *
	 * @param total The total number of votes cast.
	 * @return `true` if the required percentage of NO votes is reached, `false` otherwise.
	 */
	fun isPassedNegative(total: Int): Boolean {
		val isEnded = getRatio(forNo.get(), total) >= percentageRatio
		if (isEnded) {
			passed.set(false)
		}
		return isEnded
	}

	/**
	 * Resets the vote state, clearing all vote counts and resetting the passed status.
	 */
	fun clear() {
		forYes.set(0)
		forNo.set(0)
		passed.set(false)
		votingMemberIds.clear()
	}

	/**
	 * Calculates the percentage of votes based on the given vote count and total number of votes.
	 *
	 * @param value The number of votes (either YES or NO).
	 * @param total The total number of votes cast.
	 * @return The percentage of the given votes out of the total.
	 */
	private fun getRatio(value: Int, total: Int) = value.toDouble() / total.toDouble() * 100
}
