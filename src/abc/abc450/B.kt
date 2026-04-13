package abc.abc450

fun main() {
    val n = readln().toInt()
    val c = Array(n + 1) { IntArray(n + 1) }
    for (i in 0 until n - 1) {
        val tmp = readln().split(" ").map { it.toInt() }.toMutableList()
        for (j in 0 until tmp.size) {
            c[i + 1][j + 2 + i] = tmp[j]
        }
    }


    for (i in 0 until n) {
        for (j in i + 1 until n) {
            for (k in j + 1 until n) {

                if (c[i + 1][j + 1] + c[j + 1][k + 1] < c[i + 1][k + 1]) {
                    println("Yes")
                    return
                }
            }
        }
    }
    println("No")

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

