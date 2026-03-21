package abc.abc404

fun main() {
    val (n, m) = readln().split(" ").map { it.toInt() }
    val c = readln().split(" ").map { it.toInt() }
    val zoo = Array(n) { mutableListOf<Int>() }
    for (i in 0 until m) {
        val animal = readln().split(" ").map { it.toInt() }
        for (j in 1 until animal.size) {
            zoo[animal[j] - 1].add(i)
        }
    }
    var num = 1
    for (i in 0 until n) {
        num *= 3
    }
    var ans = Long.MAX_VALUE

    for (i in 0 until num) {
        var tmp = i
        var money = 0L
        var animalCount = IntArray(m)
        for (j in 0 until n) {
            if (tmp % 3 == 0) {

            } else {
                money += c[j].toLong() * (tmp % 3)
            }
            for (k in zoo[j]) {
                animalCount[k] += tmp % 3
            }
            tmp /= 3
        }
        var isALLClear = true
        for (count in animalCount) {
            if (count < 2) {
                isALLClear = false
                break
            }
        }
        if (isALLClear) ans = minOf(ans, money)
    }
    println(ans)

}