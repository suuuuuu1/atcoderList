package abc.abc475

fun main() {
    val n = readln().toInt()
    val a = readln().split(" ").map { it.toInt() }
    var ans = IntArray(3)
    for (i in 0 until a.size) {
        var t = a[i]
        var t2 = mutableListOf<Int>()
        for (j in 0 until 3) {
            t2.add(t % 10)
            t /= 10
        }
        val t4 = (t2[0] +t2[1]*10 +t2[2]*100)
        var t3 = 1000 - t4

        for (j in 0 until 3) {
            val t5 = t3 % 10
            t3/=10
            if (t5  == 0) continue
            ans[j] += t5
        }


    }

    println(ans.joinToString(" "))
}