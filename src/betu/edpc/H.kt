package betu.edpc

fun main() {
    val (h, w) = readln().split(" ").map { it.toInt() }
    val grid = Array(h) { readln().toCharArray() }
    val mod = 1e9.toLong() + 7
    val dp = Array(h) { LongArray(w) }
    dp[0][0] = 1

    for (i in 0 until h) {
        //0は右 1が左
        for (j in 0 until w) {
            if (grid[i][j] == '#') continue
            if (j != 0) {
                dp[i][j] = (dp[i][j] + dp[i][j - 1]) % mod
            }
            if (i != 0) {
                dp[i][j] = (dp[i][j] + dp[i - 1][j]) % mod
            }
        }
    }
    println(dp[h-1][w-1])

}