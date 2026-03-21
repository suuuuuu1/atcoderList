package abc.abc291

fun main() {
    val n = readln().trim().toInt()
    val ab = Array(n) { readln().trim().split(" ").map { it.toInt() } }
    val mod = 998244353L

    var dpA = 1L // i枚目をA_iで終える通り数
    var dpB = 1L // i枚目をB_iで終える通り数

    for (i in 1 until n) {
        val prevA = ab[i - 1][0]
        val prevB = ab[i - 1][1]
        val curA = ab[i][0]
        val curB = ab[i][1]

        var nextA = 0L
        var nextB = 0L

        if (curA != prevA) nextA = (nextA + dpA) % mod
        if (curA != prevB) nextA = (nextA + dpB) % mod

        if (curB != prevA) nextB = (nextB + dpA) % mod
        if (curB != prevB) nextB = (nextB + dpB) % mod

        dpA = nextA
        dpB = nextB
    }

    println((dpA + dpB) % mod)
}