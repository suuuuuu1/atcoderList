package betu.edpc

import kotlin.math.max

fun main() {
    val (n, w) = readln().split(" ").map { it.toInt() }
    val wv = Array(n) { readln().split(" ").map { it.toInt() } }
    val dp = Array(n + 1) { LongArray(w + 1) }
    for (i in 0 until n) {
        val weight = wv[i][0]
        val value = wv[i][1].toLong()
        for (j in 0..w) {
            if (j >= weight) {
                dp[i + 1][j] = max(dp[i][j], dp[i][j - weight] + value)
            }else{
                dp[i+1][j] = dp[i][j]
            }


            System.out.printf("%6s", "i \\ j|")
            for (j in 0..w) {
                System.out.printf("%5d", j)
            }
            println()
            println("-----|------------------------------------------------")


            // テーブルの中身
            for ((rowIdx, row) in dp.withIndex()) {
                System.out.printf("%4d |", rowIdx)
                for (v in row) {
                    System.out.printf("%5d", v)
                }
                println()
            }
            println()
        }

    }

    println(dp[n][w])
}


