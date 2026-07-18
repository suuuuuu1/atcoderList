package abc.abc466

fun main() {
    val n = readln().toInt()
    var i = 1
    var j = 2
    var count = 0
    var now = 0
    for (k in 0 until n * 2) {
        println("? $i ${j + count}")
        val ans = readln()

        if (ans == "Yes") {
            count++
        } else if (ans == "No" && count != 0) {
            i += count
            j = i + 1

            now += (count * (count + 1)) / 2
            count = 0

            continue
        } else {
            i += 1
            j = i + 1
        }

        if (j + count > n && count == 0) {
            println("! $now")
            return
        } else if (j + count > n && count != 0) {
            now += (count * (count + 1)) / 2
            i += count
            j = i + 1
            count = 0
        }

        if (i >= n) break
    }
    println("! $now")
}