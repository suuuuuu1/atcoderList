package betu.edpc

import kotlin.math.abs

fun main() {
    val (n, step) = readln().split(" ").map { it.toInt() }
    val h = readln().split(" ").map { it.toInt() }
    val dp = IntArray(n)
    for(i in 1 until n){
        dp[i] = dp[i-1] + abs(h[i-1] - h[i])
        for(j in 2 .. step){
            if(i - j < 0)break
            dp[i] = minOf(dp[i], dp[i-j]+abs(h[i-j] - h[i]))
        }
    }
    println(dp[n-1])
}
