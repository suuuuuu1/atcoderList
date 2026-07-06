package abc.abc460

fun main() {
    val (n, m) = readln().split(" ").map { it.toInt() }
    val a = readln().split(" ").map { it.toLong() }.sorted()
    val b = readln().split(" ").map { it.toLong() }.sorted()
    var ans = 0
    for (i in 0 until a.size) {
        if  (ans >= b.size) break
        if (b[ans] <= a[i] * 2) {
            ans++
        }
    }
    for (i in 0 until a.size) {
        if  (ans >= b.size) break
        if (b[ans] <= a[i] * 2) {
            ans++
        }
    }

    println(ans)
}