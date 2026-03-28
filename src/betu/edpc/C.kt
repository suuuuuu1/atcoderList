package betu.edpc

fun main() {
    val n = readln().toInt()
    var ans = 0L
    val dp = Array(n+1){LongArray(3)}
    for (i in 0 until n) {
        val abc = readln().split(" ").map{it.toInt()}
        for(j in 0 until 3){
            for(k in 0 until 3){
                if(j == k )continue
                dp[i+1][k] = maxOf(dp[i][j] + abc[k],dp[i+1][k])
            }
        }
    }
    ans = dp[n].maxOrNull()?:0
    println(ans)
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
