package betu.edpc

import kotlin.math.*

fun main() {
    val (n, w) = readln().split(" ").map { it.toInt() }
    val wv = Array(n) { readln().split(" ").map { it.toInt() } }
    val maxV = wv.sumOf { it[1] }
    val INF = 1_000_000_000_000_000_000L
    val dp = Array(n + 1) { LongArray(maxV + 1) { INF } }
    dp[0][0] = 0

    for (i in 0 until n) {
        val weight = wv[i][0]
        val value = wv[i][1]
        for (j in 0 .. maxV) {
            if (j >= value) {
                dp[i + 1][j] = min(dp[i][j - value] + weight, dp[i][j])
            } else {
                dp[i + 1][j] = dp[i][j]
            }
        }
    }
    var ans = 0
    for(i in 0 .. maxV){
        if(dp[n][i] > w)continue
        ans = i
    }
    println(ans)
}


