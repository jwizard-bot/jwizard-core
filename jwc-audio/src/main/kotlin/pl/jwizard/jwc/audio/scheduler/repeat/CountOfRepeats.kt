package pl.jwizard.jwc.audio.scheduler.repeat

class CountOfRepeats {
	var current = 0
		private set

	private var total = 0

	fun set(count: Int) {
		this.current = count
		this.total = count
	}

	fun decrease() {
		current -= 1
	}

	fun clear() {
		current = 0
		total = 0
	}

	val currentRepeat
		get() = total - current
}
