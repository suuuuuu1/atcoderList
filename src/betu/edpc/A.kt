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
    val n = readln().trim().toInt()
    val h = readln().trim().split(" ").map { it.toInt() }

    val INF = 1 shl 30
    val dp = IntArray(n) { INF }
    dp[0] = 0

    for (i in 0 until n) {
        if (i + 1 < n) chmin(dp, i + 1, dp[i] + abs(h[i] - h[i + 1]))
        if (i + 2 < n) chmin(dp, i + 2, dp[i] + abs(h[i] - h[i + 2]))
    }

    println(dp[n - 1])
}

