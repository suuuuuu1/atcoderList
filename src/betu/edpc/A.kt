package betu.edpc

import kotlin.math.abs
fun main() {
    val n = readln().toInt()
    val h  = readln().split(" ").map{it.toInt()}
    val dp = IntArray(n)
    for(i in 1 until n){
        dp[i] = dp[i-1] + abs(h[i]-h[i-1])
        if(i >= 2)dp[i] = minOf(dp[i],dp[i-2]+abs(h[i] - h[i-2]))
    }
    println(dp[n-1])
}

