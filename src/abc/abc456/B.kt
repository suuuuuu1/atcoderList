package abc.abc456

fun main() {
    val d1 = readln().split(" ").map { it.toInt() }
    val d2 = readln().split(" ").map { it.toInt() }
    val d3 = readln().split(" ").map { it.toInt() }
    val temp = IntArray(3)
    var ans = 0

    for (i in 0 until d1.size) {
        for (j in 0 until d2.size) {
            for (k in 0 until d3.size) {
                temp[0] = d1[i]
                temp[1] = d2[j]
                temp[2] = d3[k]

                val a = temp.sorted()

                if (a[0] == 4 && a[1] == 5 && a[2] == 6) {
                    ans++
                }
            }
        }
    }
    println((ans.toDouble()) / (6 * 6 * 6))
}