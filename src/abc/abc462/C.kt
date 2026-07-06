package abc.abc462

import com.sun.source.tree.Tree
import com.sun.source.util.Trees
import java.util.TreeSet

fun main() {
    val n = readln().toInt()
    val xy = Array(n) { readln().split(" ").map { it.toInt() } }.sortedBy { it.first() }

    var ans = n

    var min = 100000000
    var max = 0
    for (ints in xy) {
        val (a, b) = ints
        min = minOf(min, b)
        if (a == 1 || b == 1) continue
        if(b > min)ans--
    }
    println(ans)
}