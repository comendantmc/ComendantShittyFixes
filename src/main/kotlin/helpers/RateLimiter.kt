package helpers

class RateLimiter(val limit: Int, val interval: Long) {
    var available = 0.0
    var lastTimeStamp: Long = System.currentTimeMillis()

    fun canAdd(): Boolean {
        val now = System.currentTimeMillis()
        // more token are released since last request
        available += (now - lastTimeStamp) * 1.0 / interval * limit
        if (available > limit) available = limit.toDouble()
        return if (available < 1) false else {
            available--
            lastTimeStamp = now
            true
        }
    }
}