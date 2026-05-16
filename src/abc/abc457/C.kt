package abc.abc457

import kotlin.math.abs

fun main() {
    val (n, k) = readln().split(" ").map { it.toLong() }
    val lArray = Array(n.toInt()) { readln().split(" ").drop(1).map { it.toInt() } }
    val c = readln().split(" ").map { it.toLong() }
    var t = k - 1
    for (i in 0 until n.toInt()) {
        val lArraySize = lArray[i].size.toLong()
        val total = c[i] * lArraySize
        if (t >= total) {
            t -= total
        } else {
            println(lArray[i][(t % lArraySize).toInt()])
            break
        }
    }
}