package betu.edpc

import kotlin.math.abs



fun main() {
    val n = readln().trim().toInt()
    val h = readln().trim().split(" ").map { it.toInt() }

    val dp = IntArray(n)
    dp[1] = Math.abs(h[0] - h[1])

    for(i in 2 until n){
        val one = Math.abs(h[i-2] - h[i])
        val two =Math.abs(h[i-1] - h[i])
        val t = minOf(dp[i-2] + one , dp[i-1] + two)
        dp[i] = t
    }
    println(dp[n-1] )
}

