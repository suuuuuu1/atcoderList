package abc.abc173

fun main() {
    val n = readln().trim().toInt()
    val a = readln().trim().split(" ").map { it.toLong() }.sortedDescending()

    var ans = 0L
    for (i in 1 until n) {
        ans += a[i / 2]
    }
    println(ans)
}
