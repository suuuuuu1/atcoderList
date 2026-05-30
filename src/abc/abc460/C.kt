package abc.abc460

fun main() {
    val (n, m) = readln().split(" ").map { it.toInt() }
    val a = readln().split(" ").map { it.toLong() }.sorted()
    val b = readln().split(" ").map { it.toLong() }.sorted()
    var skip = 0
    var ans = 0
    var nowB = 0

    for (i in 0 until a.size) {
        if (skip + nowB >= b.size) break
        if (b[skip + nowB] <= a[i] * 2) {
            ans++
            nowB++

        }

    }
    println(ans)
}