package abc.abc369

fun main() {
    val n = readln().toInt()
    val a = readln().split(" ").map { it.toLong() }
    val dp = Array(n + 1) { LongArray(2) { -1 } }

    dp[0][0] = 0

    for (i in 1..n) {
        dp[i][0] = dp[i - 1][0]
        dp[i][1] = dp[i - 1][1]

        if (dp[i][1] != -1L) {
            dp[i][0] = maxOf(dp[i][0], dp[i - 1][1] + a[i - 1] * 2)
        }
        if(dp[i][0] != -1L) {
            dp[i][1] = maxOf(dp[i][1], dp[i - 1][0] + a[i - 1])
        }
    }
    println(maxOf(dp[n].first(), dp[n].last()))

}