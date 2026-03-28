package betu.edpc

fun main() {
    val (n, w) = readln().trim().split(" ").map { it.toInt() }

    val items = Array(n) {
        val (wi, vi) = readln().trim().split(" ").map { it.toInt() }
        wi to vi
    }

    // dp[weight] = その重さ以下で達成できる最大価値
    val dp = LongArray(w + 1) { 0L }

    for (i in 0 until n) {
        val wi = items[i].first
        val vi = items[i].second.toLong()

        // 0/1 ナップサックなので後ろから更新
        for (curW in w downTo wi) {
            val cand = dp[curW - wi] + vi
            if (cand > dp[curW]) dp[curW] = cand
        }
    }

    println(dp[w])
}

@Suppress("unused")
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
