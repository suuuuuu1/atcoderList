package abc.abc455

import java.util.HashMap

fun main() {
    val (n,k) = readln().split(" ").map{it.toInt()}
    val a = readln().split(" ").map{it.toInt()}
    var ans = 0L

    val hash = HashMap<Long,Long>()
    for (i in a) {
        hash.put(i.toLong(),hash.getOrDefault(i.toLong(),0)+i.toLong())
    }
    val t = mutableListOf<Long>()
    for (value in hash.values) {
        t.add(value)
    }
    t.sort()


    for(i in 0 until t.size - k){
        ans += t[i]
    }
    println(ans)
}