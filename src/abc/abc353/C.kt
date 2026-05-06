package abc.abc353

fun main() {
    val n = readln().toInt()
    val a = readln().split(" ").map { it.toLong() }.toLongArray()
    val mod = Math.pow(10.0, 8.0).toLong()
    var totalSum = 0L
    for (x in a) {
        totalSum += x * (n - 1)
    }
    a.sort()
    var count = 0L
    var r = n - 1
    for (l in 0 until n) {
        while (r > l && a[l] + a[r] >= mod) {
            r--
        }
        val num = n - 1 - maxOf(l, r)
        count += num

    }
    println(totalSum - count * mod)
}