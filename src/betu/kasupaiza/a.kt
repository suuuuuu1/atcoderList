package betu.kasupaiza

fun main(args: Array<String>) {
    val (h, w) = readln().split(" ").map { it.toInt() }
    val s = Array(h) { readln().split(" ").map { it.toInt() } }
    val dp = Array(h+1) { IntArray(w) }
    val dir = intArrayOf(-1, 0, 1)
    for (i in 0 until h) {
        for (j in 0 until w) {
            for (k in dir) {
                if (!(j + k in 0 until w)) continue
                dp[i + 1][j + k] = maxOf(dp[i][j] + s[i][j + k], dp[i + 1][j + k])
            }
        }
    }
    println(dp[h].max())
}