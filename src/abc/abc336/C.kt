package abc.abc336

fun main() {
    var n = readln().toLong() - 1
    if (n == 0L) {
        println(0)
        return
    }
    val result = mutableListOf<Long>()
    while (n > 0L) {
        result.add((n % 5) * 2)
        n /= 5
    }
    println(result.reversed().joinToString(""))
}
