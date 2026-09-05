package betu.edpc

import kotlin.math.abs

fun main() {
    val (n, k) = readln().split(" ").map { it.toInt() }
    val h = readln().split(" ").map { it.toInt() }
    val dp = IntArray(n){Int.MAX_VALUE}
    dp[0] = 0

    for (i in 1 until n) {
         for (j in 1 ..  k) {
             if(i - j >= 0){
                 val cost = dp[i - j] + abs(h[i] - h[i-j])
                 dp[i] = minOf(dp[i],cost)
             }
         }
    }
    println(dp[n-1])
}
