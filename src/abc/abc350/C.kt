
fun main() {
    val n = readln().toInt()
    val a = readln().split(" ").map { it.toInt() }.toIntArray()
    val numIndex = IntArray(n)
    for (i in 0 until n) {
        numIndex[a[i] - 1] = i
    }
    val sb = StringBuilder()
    var count = 0

    for (i in 0 until n) {
        val t = numIndex[i]
        if (t != i) {
            count++
            val v = a[i]

            a[i] = a[t]
            a[t] = v

            numIndex[i] = i
            numIndex[v - 1] = t

            sb.append(i + 1).append(" ").append(t + 1).append("\n")
        }
    }

    println(count)
    print(sb)
}
