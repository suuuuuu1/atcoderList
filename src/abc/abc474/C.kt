package abc.abc474

import java.util.TreeSet

fun main() {
    val (n,q) = readln().split(" ").map{it.toInt()}
    val p = readln().split(" ").map{it.toInt()}
    val a = ArrayDeque<Int>()
    val set = HashSet<Int>()
    for(i in 0 until q){
        val t = readln().toInt()
        set.add(t)
        a.add(t)
    }
    val SB2 = StringBuilder()
    for (i in p) {
        if(!set.contains(i)){
            SB2.append(i).append(" ")
        }
    }

    val SB = StringBuilder()
    val t = mutableListOf<Int>()
    for (i in a.size-1 downTo 0) {
        if (set.contains(a[i])){
            t.add(a[i])
            set.remove(a[i])
        }

    }
    t.reverse()

    for (i in t) {
        SB.append(i).append(" ")
    }

    SB2.append(SB)
    println(SB2)


}
