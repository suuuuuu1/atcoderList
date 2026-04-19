package abc.abc451

import java.util.TreeSet

fun main() {
    val q = readln().toInt()
    val sb = StringBuilder()
    var count = 0
    val map = HashMap<Long, Int>()
    val set = TreeSet<Long>()
    for (i in 0 until q) {
        val (query, h) = readln().split(" ").map { it.toLong() }
        when (query.toInt()) {
            1 -> {
                count++
                set.add(h)
                sb.append("$count\n")
                map.put(h, map.getOrDefault(h, 0) + 1)
            }
            2 -> {
                while (true) {
                    if (set.isNotEmpty()) {
                        val t = set.removeFirst()
                        if (t <= h) {
                            val t3 = map.getOrDefault(t, 0)
                            count -= t3
                            map.put(t, 0)
                        } else {
                            set.add(t)
                            break
                        }
                    } else {
                        break
                    }
                }
                sb.append("$count\n")
            }
        }

    }
    println(sb)
}

@Suppress("unused")
private fun chmin(a: IntArray, idx: Int, v: Int): Boolean {
    if (v < a[idx]) {
        a[idx] = v
        return true
    }
    return false
}

@Suppress("unused")
private fun chmax(a: IntArray, idx: Int, v: Int): Boolean {
    if (v > a[idx]) {
        a[idx] = v
        return true
    }
    return false
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