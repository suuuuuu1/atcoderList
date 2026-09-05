package betu.edpc

fun main() {
    val n = readln().toInt()
    val dp = Array(100_010){IntArray(3)}
    for(i in 0 until n){
        val a = readln().split(" ").map{it.toInt()}
        for(j in 0 until 3){
            for(k in 0 until 3){
                if(j == k)continue
                dp[i+1][k] = maxOf(dp[i][j] + a[k],dp[i+1][k])
            }
        }
    }
    println(dp[n].max())
}

