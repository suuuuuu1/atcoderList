package abc.abc172

fun main() {
    val (n, m, k) = readln().split(" ").map { it.toInt() }
    val a = readln().split(" ").map { it.toLong() }
    val b = readln().split(" ").map { it.toLong() }
    val ruisekiA = a.runningFold(0L ,Long::plus)
    val ruisekiB = b.runningFold(0L,Long::plus)
    var ans = 0
    var ok = m
    for (i in 0 until n + 1) {
        while (0 <= ok && k < ruisekiA[i] + ruisekiB[ok]) ok--;
        if (0 <= ok) ans = maxOf(ans, i + ok)
    }
    println(ans)
    a.runningFold(0L,Long::plus)
}