package helpers

class MutableInt {
    var value = 1 // note that we start at 1 since we're counting

    fun increment() {
        ++value
    }

    fun get(): Int {
        return value
    }
}