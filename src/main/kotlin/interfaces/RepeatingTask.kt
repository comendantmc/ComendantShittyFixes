package interfaces

interface RepeatingTask: Runnable {
    val delay: Long
    val period: Long
}