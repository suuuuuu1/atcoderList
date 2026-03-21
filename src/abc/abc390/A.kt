package abc.abc390

fun main() {
    val a = readln().split(" ").map { it.toInt() }.toIntArray()
    val target = intArrayOf(1, 2, 3, 4, 5)

    for (i in 0 until 4) {
        val b = a.clone()
        val tmp = b[i]
        b[i] = b[i + 1]
        b[i + 1] = tmp

        if (b.contentEquals(target)) {
            println("Yes")
            return
        }
    }

    println("No")
}
