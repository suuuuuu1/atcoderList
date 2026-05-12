package abc.abc172

fun main() {
    val (n, m, k) = readln().split(" ").map { it.toInt() }
    val a = readln().split(" ").map { it.toLong() }
    val b = readln().split(" ").map { it.toLong() }
    val ruisekiA = LongArray(n + 1)
    val ruisekiB = LongArray(m + 1)
    for (i in 0 until a.size) {
        ruisekiA[i + 1] = ruisekiA[i] + a[i]
    }
    for (i in 0 until b.size) {
        ruisekiB[i + 1] = ruisekiB[i] + b[i]
    }
    var ans = 0
    var ok = m
    for (i in 0 until n + 1) {
        while (0 <= ok && k < ruisekiA[i] + ruisekiB[ok]) ok--;
        if (0 <= ok) ans = maxOf(ans, i + ok)
    }
    println(ans)

}