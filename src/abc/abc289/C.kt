package abc.abc289

fun main() {
    val (n, m) = readln().split(" ").map { it.toInt() }
    val c = IntArray(m)
    val a = Array(m) { mutableListOf<Int>() }
    var ans = 0
    repeat(m) {
        c[it] = readln().toInt()
        a[it] = readln().split(" ").map { it.toInt() }.toMutableList()
    }
    val t = 1 shl m

    for (i in 0 until t) {
        val temp = mutableSetOf<Int>()


        for (j in 0 until m) {
            if (i and (1 shl j) == 0) {
                temp += a[j]
            }

        }

        var gg = true
        for(j in 1 .. n ){
            if(!temp.contains(j)) {
                gg = false
                break
            }

        }
        if(gg && temp.size == n) ans ++


    }
    println(ans)
}
