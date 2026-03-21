package abc.abc306

fun main() {
    val n = readln().trim().toInt()

    val negInf = Long.MIN_VALUE / 4
    var dp = longArrayOf(0L, negInf)
    // dp[0]: 生存かつ健康, dp[1]: 生存かつ毒状態

    repeat(n) {
        val (x, yInt) = readln().trim().split(" ").map { it.toInt() }
        val y = yInt.toLong()

        val next = longArrayOf(negInf, negInf)

        // 食べない: 状態はそのまま
        chmax(next, 0, dp[0])
        chmax(next, 1, dp[1])

        if (x == 0) {
            // 解毒皿(安全): 健康/毒のどちらからでも健康へ遷移できる
            chmax(next, 0, dp[0] + y)
            chmax(next, 0, dp[1] + y)
        } else {
            // 毒皿: 健康から食べたときだけ毒状態へ遷移できる
            chmax(next, 1, dp[0] + y)
        }

        dp = next
    }

    println(maxOf(dp[0], dp[1]))
}

@Suppress("unused")
private fun chmin(a: LongArray, idx: Int, v: Long): Boolean {
    if (v < a[idx]) {
        a[idx] = v
        return true
    }
    return false
}

private fun chmax(a: LongArray, idx: Int, v: Long): Boolean {
    if (v > a[idx]) {
        a[idx] = v
        return true
    }
    return false
}
