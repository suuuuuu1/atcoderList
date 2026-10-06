package abc.abc478

fun main() {
    val (n, v) = readln().split(" ").map { it.toInt() }
    val w = readln().split(" ").map { it.toInt() }
    var max = 0L
    for (i in 1 until n+1) {
        for (j in 1 until n+1) {
            for (k in 1 until n+1) {
                if (i + j + k <= v && i != j && j != k && k != i)
                    max = maxOf(max, (w[i-1] + w[j-1] + w[k-1]).toLong())
            }
        }
    }
    println(max)
}