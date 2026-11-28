package abc.abc486

//https://suuuuuu1.github.io/atcoderList/#
fun main() {
    val n = readln().toInt()
    val lp = Array(n) {
        readln().split(" ").map { it.toInt() }
    }

    val INF = 1_000_000_000_000_000_000L
    val dp = Array(n + 1) { LongArray(n + 1) { INF } }

    dp[0][1] = 0

    for (i in 1 until n) {
        val level = lp[i - 1][0]
        val cost = lp[i - 1][1]

        for (j in 1..i + 1) {
            dp[i][j] = dp[i - 1][j]

            if (j - 1 >= level) {
                dp[i][j] = minOf(
                    dp[i][j],
                    dp[i - 1][j - 1] + cost
                )
            }
        }
    }

    val level = lp[n - 1][0]
    val cost = lp[n - 1][1]
    if (level <= n && dp[n - 1][level] < INF) {
        println(dp[n - 1][level] + cost)
    } else {
        println(-1)
    }
}