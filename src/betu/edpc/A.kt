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

    val dp = IntArray(n)
    dp[0] = 0
    if (n >= 2) dp[1] = abs(h[1] - h[0])

    for (i in 2 until n) {
        val oneStep = dp[i - 1] + abs(h[i] - h[i - 1])
        val twoStep = dp[i - 2] + abs(h[i] - h[i - 2])
        chmin(dp, i, oneStep)
        chmin(dp, i, twoStep)
    }
    println(dp[n - 1])
}
