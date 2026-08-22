package abc.abc472

fun main() {
    val n = readln().toInt()
    val g = readln().split(" ").map { it.toInt() }
    var min = Int.MAX_VALUE
    for (i in 0 until n - 1) {
        var l = 0
        var r = 0
        for (j in 0 until n) {
            if (j <= i) {
                l += g[j]
            } else {
                r += g[j]
            }
        }
        min = minOf(min, kotlin.math.abs(l - r))
    }
    println(min)
}