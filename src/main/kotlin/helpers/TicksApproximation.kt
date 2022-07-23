package helpers

object TicksApproximation {
    fun ticksInMinutes(ticks: Int, averageTPS: Double = 20.0): Double {
        return ticks / averageTPS / 60
    }

    fun ticksInHours(ticks: Int, averageTPS: Double = 20.0): Double {
        return ticks / averageTPS / 60 / 60
    }

    fun ticksInDays(ticks: Int, averageTPS: Double = 20.0): Double {
        return ticks / averageTPS / 60 / 60 / 24
    }
}