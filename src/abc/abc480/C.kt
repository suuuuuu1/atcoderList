package abc.abc480

// genre:
fun main() {
    val (n, k, t) = readln().split(" ").map { it.toInt() }
    val a = readln().split(" ").map { it.toInt() }.sorted()

    var ans = 0
    var end = 0

    for (x in a + t) {
        if (end < x) {
            val cnt = (x - end + k - 1) / k
            ans += cnt
            end += cnt * k
        }
        if (x != t) {
            end = maxOf(end, x + k)
        }
    }

    println(ans)
}