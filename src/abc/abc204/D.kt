package abc.abc204

//https://suuuuuu1.github.io/atcoderList/#
fun main() {
    val n = readln().toInt()
    val t = readln().split(" ").map { it.toInt() }

    val sum = t.sum()
    val dp = BooleanArray(sum + 1)
    dp[0] = true

    for (x in t) {
        for (i in sum downTo x) {
            if (dp[i - x]) {
                dp[i] = true
            }
        }
    }
    var ans = sum
    var min = Int.MAX_VALUE
    for (i in 0 until dp.size) {
        if (dp[i]) {
            min = minOf(min, maxOf(i, ans - i))
        }
    }
    println(min)
}