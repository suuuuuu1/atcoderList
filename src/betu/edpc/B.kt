package betu.edpc

import kotlin.math.abs

private fun chmin(a: IntArray, idx: Int, v: Int): Boolean {
    if (v < a[idx]) {
        a[idx] = v
        return true
    }
    return false
}

@Suppress("unused")
private fun chmax(a: IntArray, idx: Int, v: Int): Boolean {
    if (v > a[idx]) {
        a[idx] = v
        return true
    }
    return false
}

@Suppress("unused")
private fun chmin(a: LongArray, idx: Int, v: Long): Boolean {
    if (v < a[idx]) {
        a[idx] = v
        return true
    }
    return false
}

@Suppress("unused")
private fun chmax(a: LongArray, idx: Int, v: Long): Boolean {
    if (v > a[idx]) {
        a[idx] = v
        return true
    }
    return false
}

fun main() {
    val (n, k) = readln().split(" ").map { it.toInt() }
    val h = readln().split(" ").map { it.toInt() }
    val dp = LongArray(n) { Long.MAX_VALUE }
    dp[0] = 0L
    for (i in 0 until n) {
        for (j in 1..k) {
            if (i + j < n) chmin(dp, i + j, dp[i] + abs(h[i] - h[i + j]).toLong())
        }
    }
    println(dp[n - 1])
}
