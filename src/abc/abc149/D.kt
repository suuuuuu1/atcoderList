fun main() {
    val (n, k) = readln().trim().split(" ").map { it.toInt() }
    val (r, s, p) = readln().trim().split(" ").map { it.toInt() }
    val t = readln().trim()


    var answer = 0L

    for (start in 0 until k) {
        var dp = longArrayOf(0L, 0L, 0L) // 0:r, 1:s, 2:p

        var i = start
        while (i < n) {
            val next = longArrayOf(Long.MIN_VALUE / 4, Long.MIN_VALUE / 4, Long.MIN_VALUE / 4)

            for (prevHand in 0..2) {
                for (hand in 0..2) {
                    if (hand == prevHand) continue

                    val gain = when (hand) {
                        0 -> if (t[i] == 's') r.toLong() else 0L
                        1 -> if (t[i] == 'p') s.toLong() else 0L
                        else -> if (t[i] == 'r') p.toLong() else 0L
                    }

                    val cand = dp[prevHand] + gain
                    if (cand > next[hand]) next[hand] = cand
                }
            }

            dp = next
            i += k
        }

        answer += maxOf(dp[0], dp[1], dp[2])
    }

    println(answer)
}
