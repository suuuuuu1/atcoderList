package abc.abc461

import java.util.*
import kotlin.collections.HashMap

fun main() {
    val (n, k, m) = readln().split(" ").map { it.toInt() }
    val a = Array(n+1){ mutableListOf<Long>()}
    repeat(n) {
        val (c, v) = readln().split(" ").map { it.toInt() }
        a[c].add(v.toLong())
    }
    val top = mutableListOf<Long>()
    val tail = mutableListOf<Long>()

    for (r in a) {
        if(r.isNotEmpty()){
            r.sortDescending()
            top.add(r[0])
            tail.addAll(r.drop(1))
        }
    }

    top.sortDescending()
    if(top.size > m){
        tail.addAll(top.drop(m))
    }
    tail.sortDescending()
    val ans = top.take(m).sum() + tail .take(k-m).sum()
    println(ans)
}

