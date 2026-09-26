package abc.abc477

fun main() {
    val q = readln().toInt()
    val s = readln()
    val t = readln()

    val pos = mutableListOf<Int>()
    val m = t.length

    for (i in 0..s.length - m) {
        var match = true
        for (j in 0 until m) {
            if (s[i + j] != t[j]) {
                match = false
                break
            }
        }
        if (match) {
            pos.add(i)
        }
    }

    val sb = StringBuilder()
    println(pos)
    for (i in 0 until q) {
        val parts = readln().split(" ")
        val l = parts[0].toInt() - 1
        val r = parts[1].toInt() - 1

        if (r - l + 1 < m) {
            sb.append("No\n")
            continue
        }

        var idx = pos.binarySearch(l)
        println(idx)
        if (idx < 0) {
            idx = -(idx + 1)
        }

        if (idx < pos.size && pos[idx] <= r - m + 1) {
            sb.append("Yes\n")
        } else {
            sb.append("No\n")
        }
    }
    print(sb.toString())
}