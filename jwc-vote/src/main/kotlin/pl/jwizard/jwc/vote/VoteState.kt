package pl.jwizard.jwc.vote

import net.dv8tion.jda.api.entities.Member
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class VoteState(private val percentageRatio: Int) {
	private val forYes = AtomicInteger(0)
	private val forNo = AtomicInteger(0)
	private val passed = AtomicBoolean(false)
	private val votingMemberIds = mutableSetOf<Long>()

	val votes
		get() = Pair(forYes.get(), forNo.get())

	val isPassed
		get() = passed.get()

	fun isMemberReVoting(member: Member) = votingMemberIds.contains(member.idLong)

	fun swapYesToNoVote(): Pair<String, String> {
		forYes.decrementAndGet()
		forNo.incrementAndGet()
		return Pair("YES", "NO")
	}

	fun swapNoToYesVote(): Pair<String, String> {
		forNo.decrementAndGet()
		forYes.incrementAndGet()
		return Pair("NO", "YES")
	}

	fun addYesVote(member: Member): String {
		forYes.incrementAndGet()
		votingMemberIds.add(member.idLong)
		return "YES"
	}

	fun addNoVote(member: Member): String {
		forNo.incrementAndGet()
		votingMemberIds.add(member.idLong)
		return "No"
	}

	fun isPassedPositive(total: Int): Boolean {
		val isEnded = getRatio(forYes.get(), total) >= percentageRatio
		if (isEnded) {
			passed.set(true)
		}
		return isEnded
	}

	fun isPassedNegative(total: Int): Boolean {
		val isEnded = getRatio(forNo.get(), total) >= percentageRatio
		if (isEnded) {
			passed.set(false)
		}
		return isEnded
	}

	fun clear() {
		forYes.set(0)
		forNo.set(0)
		passed.set(false)
		votingMemberIds.clear()
	}

	private fun getRatio(value: Int, total: Int) = value.toDouble() / total.toDouble() * 100
}
